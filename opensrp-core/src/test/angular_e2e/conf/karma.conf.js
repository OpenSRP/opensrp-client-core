module.exports = function (config) {
    config.set({
        basePath: '../',
        frameworks: ['ng-scenario'],
        autoWatch: true,
        browsers: ['PhantomJS'],
        proxies: {
            '/': 'http://localhost:8888/'
        },
        'junitReporter': {
            outputFile: 'test_out/e2e.xml',
            suite: 'e2e'
        },
        urlRoot: '/tests/',
        plugins: ['karma-ng-scenario', 'karma-phantomjs-launcher'],
        exclude: [
            'assets/js/lib/bootstrap.js',
            'assets/js/lib/bootstrap.min.js',
            'assets/js/lib/jquery-ui-1.8.23.custom.min.js'
        ],
        files: [
            '*.js'
        ]
    })
};
