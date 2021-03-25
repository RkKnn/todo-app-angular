#!/bin/bash

angularProjectDir=../todo-angular
angularProjectOutputDir=$angularProjectDir/dist/todo-angular
buildFilePrefix=app-angular
buildCssFile=styles.css
jsDir=public/javascripts
cssDir=public/stylesheets

build() {
  cd $angularProjectDir
  yarn run build:elements
  cd -
}

copy_to_play() {
  # js
  # cat $angularProjectOutputDir/runtime.js $angularProjectOutputDir/main.js $angularProjectOutputDir/polyfills.js > $jsDir/app-angular-main.js
  cp -f $angularProjectOutputDir/runtime.js $jsDir/app-angular-runtime.js
  cp -f $angularProjectOutputDir/main.js $jsDir/app-angular-main.js
  cp -f $angularProjectOutputDir/polyfills.js $jsDir/app-angular-polyfills.js

  # css
  for file in `find $angularProjectOutputDir -maxdepth 1 -type f -name \*"$buildCssFile"\* `; do
    cp -f $file $cssDir/elements.css
  done

}

build
copy_to_play
