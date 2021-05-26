module.exports = function (config) {
    config.set({
        basePath: '../../../../',
        frameworks: ['jasmine'],
        exclude: [
            'assets/js/lib/bootstrap.js',
            'assets/js/lib/bootstrap.min.js',
            'assets/js/lib/jquery-ui-1.8.23.custom.min.js'
        ],
        files: [
            'assets/js/lib/*.js',
            'src/test/js/lib/*.js',
            'assets/js/page_view.js',
            'assets/js/village.js',
            'assets/js/form_context.js',
            'assets/js/anmNavigation.js',
            'assets/js/smart_registry/app.js',
            'assets/js/smart_registry/*.js',
            'src/test/js/smart_registry/*spec.js'
        ]
    })
};