declare variable $path external := '/';

xslt:transform(doc('mydatabase/' || $path), doc('asdf'))