module.exports = function(grunt) {
	

	grunt.initConfig({
		jshint: { /* JavaScript lint */
			files: ['Gruntfile.js', '${basedir}/src/main/javascript/*.js']
		},
		pkg: grunt.file.readJSON('package.json'),
		browserify: {
			dist: {
				src: '<%= pkg.main %>',
				dest: '${basedir}/target/classes/js/<%= pkg.name %>.min.js' // 'work/browserified.js'
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
			dist: {
				options: {
					mangle: false,
					banner: '/*! <%= pkg.name %> <%= grunt.template.today("yyyy-mm-dd") %> */\n',
				},
				//src: ['work/dependencies.js', 'work/module.js'],
				//dest: '${basedir}/target/classes/js/<%= pkg.name %>.min.js'
				files: {
					'${basedir}/target/classes/js/<%= pkg.name %>.min.js': 'work/browserified.js'
				}
			}
		}
	});

	// Load the plugins (declared in package.json)
	grunt.loadNpmTasks('grunt-contrib-jshint');
	grunt.loadNpmTasks('grunt-contrib-uglify');
	grunt.loadNpmTasks('grunt-browserify');
	grunt.loadNpmTasks('grunt-sass');

	grunt.registerTask('default', ['jshint', 'browserify', /*'uglify',*/ 'sass']);
};