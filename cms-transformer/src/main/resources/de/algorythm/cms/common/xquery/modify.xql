declare namespace local = "http://example.org";
declare function local:copy-replace($element as element()) {
  if ($element/self::cattext)
  then <cattext>art</cattext>
  else element {node-name($element)}  
               {$element/@*, 
                for $child in $element/node()  
                return if ($child instance of element())  
                       then local:copy-replace($child)  
                       else $child  
               }  
};
local:copy-replace(/*)