var gulp = require('gulp');
var jshint = require('gulp-jshint');
var sass = require('gulp-sass');
var sourcemaps = require('gulp-sourcemaps');
var stylishReporter = require('jshint-stylish');
var uglify = require('gulp-uglify');
var browserify = require('browserify');
var source = require('vinyl-source-stream');
var buffer = require('vinyl-buffer');
var del = require('del');
var pkg = require('./package.json');

gulp.task('default', ['browserify', 'sass'], function() {
	return gulp.src('./package.json')
		.pipe(gulp.dest('${basedir}/target/classes'));
});

gulp.task('sass', ['clean'], function() {
	return gulp.src(['${basedir}/src/main/css/default-theme/default-theme.scss'])
		.pipe(sourcemaps.init())
		.pipe(sass({outputStyle: 'compressed'}))
		.pipe(sourcemaps.write())
		.pipe(gulp.dest('${basedir}/target/classes/css'));
});

gulp.task('browserify', ['clean', 'lint'], function() {
	return browserify(pkg.main).bundle()
		.pipe(source(pkg.name + '.min.js')) // converts to vinyl src with name
		.pipe(buffer())                     // converts to vinyl buffer obj
		.pipe(uglify()) 
		.pipe(gulp.dest('${basedir}/target/classes/js'));
});

gulp.task('lint', function() {
	return gulp.src('${basedir}/src/main/javascript/**/*.js')
		.pipe(jshint())
		.pipe(jshint.reporter(stylishReporter))
		.pipe(jshint.reporter('fail'));
});

gulp.task('clean', function() {
	return del.sync('./work');
});