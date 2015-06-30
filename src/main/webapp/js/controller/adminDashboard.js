(function () {

    'use strict';

    angular.module('adminDashboard', ['constantsModule'])
        .controller ('AdminDashboardController', ['BASE', '$log', '$http', function (BASE, $log, $http) {


        this.fuffa = function () {
            $http.post(BASE + '/load-example-data')
                .success(function (data, status) {
                    $log.info('FUFFA DATA OK -> ' + status);
                })
                .error(function (data, status) {
                    $log.warn('FUFFA NOT RETRIVED -> ' + status + " " + data.error());
                });
        };
    }]);
})();