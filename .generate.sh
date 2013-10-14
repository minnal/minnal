#! /bin/bash -e

rm -rf *;
git checkout master docs/source docs/Makefile;
git reset HEAD;
cd docs;
make -f Makefile html;
mv -fv build/html/* ../;
cd ..;
rm -rf docs;
git add -A;
git commit -m "Generated gh-pages for `git log master -1 --pretty=short --abbrev-commit`";
git push origin gh-pages;
