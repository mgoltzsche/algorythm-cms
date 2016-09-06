module namespace cms = "http://cms.algorythm.de/CMS";
declare namespace s = "http://cms.algorythm.de/common/Site";
declare default element namespace "http://www.w3.org/1999/xhtml";

declare function cms:default-theme($site as node(), $page as node(), $urlPrefix as xs:string) as node() {
  let $title := cms:page-title($page)
  let $pageNav := cms:html-subnavigation($page, $urlPrefix)
  return <html>
    <head>
      <title>{$title}</title>
    </head>
    <body>
      <a href="{$urlPrefix}index.html">{$site/@title/string()}</a>
      <h1>{$title}</h1>
      <nav>
        {cms:html-navigation($site, $page, $urlPrefix)}
      </nav>
      <nav>
        {cms:html-breadcrumbs($page, $urlPrefix)}
      </nav>
      {$pageNav}
      <div>
        {cms:render-page-content($site, $page)}
      </div>
    </body>
  </html>
};

(: example method for function-lookup in renderer content attribute :)
declare function cms:generate-start-page($page as node()) as node() {
  <p>some generated content</p>
};

declare function cms:html-navigation($parentPage as node(), $currentPage as node(), $urlPrefix as xs:string) as node()* {
  if ($parentPage/*)
    then
      <ul>
        {for $child in $parentPage/s:page[empty(@nav-exclude) or xs:boolean(@nav-exclude) = false()]
            return
              <li>
                <a href="{cms:build-url($child, $urlPrefix)}" class="{if ($currentPage = $child) then 'active' else ''}">
                  {cms:page-title($child)}
                </a>
              </li>}
      </ul>
    else
      ()
};

declare function cms:html-breadcrumbs($page as node(), $urlPrefix as xs:string) as node()* {
  for $parent in $page/ancestor-or-self::s:page
    return <a href="{cms:build-url($parent, $urlPrefix)}">{cms:page-title($parent)}</a>
};

declare function cms:html-subnavigation($page as node(), $urlPrefix as xs:string) as node()* {
  if ($page/self::s:page[empty(@nav-hide) or xs:boolean(@nav-hide) = false()])
   then if ($page/s:page)
    then
      <nav>
        {cms:html-navigation($page, $page, $urlPrefix)}
      </nav>
    else if ($page/parent::s:page)
      then
        <nav>
          {cms:html-navigation($page/.., $page, $urlPrefix)}
        </nav>
      else ()
  else ()
};

declare function cms:page-title($page as node(), $content as node()) as xs:string {
  if ($page/@title/string())
    then $page/@title/string()
    else ($content/@title/string(), $page/@id/string())[1]
};

declare function cms:page-title($page as node()) as xs:string {
  if ($page/@title)
    then $page/@title/string()
    else (cms:page-content($page)/@title/string(), $page/@id/string())[1]
};

declare function cms:render-page-content($site as node(), $page as node()) as node() {
  let $renderer := if ($page/@renderer)
    then (fn:function-lookup(xs:QName($page/@renderer/string()), 1), 'renderer: '||$page/@renderer/string())[1]
    else if ($site/@default-renderer)
      then (fn:function-lookup(xs:QName($site/@default-renderer/string()), 1), 'default-renderer: '||$site/@default-renderer/string())[1]
      else cms:xslt-transform-cms-content#1
  return $renderer($page)
};

declare function cms:xslt-transform-cms-content($page as node()) as node() {
  map:get(fn:transform(map{
    'stylesheet-location': 'http://cms.algorythm.de/cms-api/html/SimpleComponents.xsl',
    'source-node': cms:page-content($page)
  }), QName('', 'output'))
};

declare function cms:page-content($page as node()) as node() {
  if ($page/@src)
      then doc('testdb/' || $page/@src/string())/*
      else if ($page/s:content)
        then $page/s:content
        else <undefined-content />
};

declare function cms:page($sitemap as node(), $pageID as xs:string*) as node()* {
  if ($pageID)
    then $sitemap//s:page[@id=$pageID]
    else $sitemap/s:site
};

declare function cms:build-url($page as node(), $urlPrefix as xs:string) as xs:string {
  $urlPrefix || fn:string-join(for $parent in $page/ancestor-or-self::s:page
    return $parent/@id/string(), '/')||'/index.html'
};

declare function cms:relative-root-path($path as xs:string) as xs:string {
  fn:string-join(for $i in 1 to count(fn:tokenize($path, '/')) return '../', '')
};

declare function cms:http-header($statusCode as xs:integer, $contentType as xs:string, $contentLanguage as xs:string) {
  <rest:response>
    <http:response status="{$statusCode}">
      <http:header name="Content-Language" value="{$contentLanguage}"/>
      <http:header name="Content-Type" value="{$contentType}"/>
    </http:response>
  </rest:response>
};


declare
%rest:path("/p")
%rest:GET
function cms:render-start-page-redirect() {
  <rest:redirect>/p/index.html</rest:redirect>
};

declare
%rest:path("/p/index.html")
%rest:GET
%output:method("xhtml")
%output:omit-xml-declaration("yes")
function cms:render-html-page() {
  cms:render-html-page('')
};

declare
%rest:path("/p/{$path=.+}/index.html")
%rest:GET
%output:method("xhtml")
%output:omit-xml-declaration("yes")
function cms:render-html-page($path as xs:string) {
  let $urlPrefix := cms:relative-root-path($path)
  let $sitemap := doc('testdb/cms-site.xml')
  let $site := $sitemap/s:site
  let $theme := if ($site/@theme/string())
    then (fn:function-lookup(xs:QName($site/@theme/string()), 3), 'theme: '||$site/@theme/string())[1]
    else cms:default-theme#3
  let $pathSegments := fn:tokenize($path, '/')
  let $page := cms:page($sitemap, $pathSegments[last()])
  return if ($page)
    then (
      let $pageUrl := cms:build-url($page, '')
      return if ($pageUrl != $path||'/index.html')
        (: Redirect to correct path when page ID found but path incorrect :)
        then <rest:redirect>{cms:build-url($page, $urlPrefix)}</rest:redirect>
        else (
          (: Show requested page :)
          cms:http-header(200, 'text/html; charset=UTF-8', 'en'),
          $theme($site, $page, $urlPrefix)
        )
    ) else (
      (: Show 404 page :)
      let $linkPage := (for $segment in fn:reverse($pathSegments) return cms:page($sitemap, $segment), $site)[1]
      let $linkUrl := cms:build-url($linkPage, $urlPrefix)
      let $content := <s:not-found title="Not found">
          <s:content>
            <a href="{$linkUrl}">Next matching page</a>
          </s:content>
        </s:not-found>
      return (
        cms:http-header(404, 'text/html; charset=UTF-8', 'en'),
        $theme($site, $content, $urlPrefix)
      )
    )
};