module.exports = function(grunt) {
	grunt.loadNpmTasks('grunt-contrib-jshint');
	grunt.initConfig({
		jshint: { /* JavaScript lint */
			files: ['Gruntfile.js', '${basedir}/src/main/javascript/*.js']
		},
		pkg: grunt.file.readJSON('package.json'),
		bower_concat: { // Concats bower dependencies into one file
			all: {
				dest: 'work/dependencies.js',
				cssDest: 'work/dependencies.css'
			}
		},
		concat: {
			module: {
				src: '${basedir}/src/main/javascript/**/*.js',
				dest: 'work/module.js'
			}
		},
		browserify: {
			options: {
				sourceMap: true
			},
			dist: {
				src: '<%= pkg.main %>',
				dest: 'work/browserified.js'
			}
		},
		sass: {
			dist: {
				options: {
					sourceMap: true
				},
				files: {
					'${basedir}/target/classes/css/default-theme/main.css': '${basedir}/src/main/css/default-theme/main.scss'
				}
			}
		},
		uglify: {
			all: {
				options: {
					banner: '/*! <%= pkg.name %> <%= grunt.template.today("yyyy-mm-dd") %> */\n',
				},
				//src: ['work/dependencies.js', 'work/module.js'],
				//dest: '${basedir}/target/classes/js/<%= pkg.name %>.min.js'
				files: {
					'${basedir}/target/classes/js/<%= pkg.name %>.min.js': 'work/browserifed.js'
				}
			}
		}
	});

	// Load the plugins (declared in package.json)
	grunt.loadNpmTasks('grunt-contrib-uglify');
	grunt.loadNpmTasks('grunt-contrib-concat');
	grunt.loadNpmTasks('grunt-bower-concat');
	grunt.loadNpmTasks('grunt-browserify');
	grunt.loadNpmTasks('grunt-sass');

	grunt.registerTask('default', ['jshint', 'bower_concat', 'concat', 'browserify', 'uglify', 'sass']);
};