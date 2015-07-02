(function () {

    'use strict';

    angular.module('adminDashboard', ['constantsModule'])
        .controller ('AdminDashboardController', ['BASE', '$log', '$http', function (BASE, $log, $http) {

        var ctrl = this;
        this.result = "";

        this.fuffa = function () {
            $('.data-loader').addClass('active');
            this.result = "CARICAMENTO...";
            $http.post(BASE + '/load-example-data')
                .success(function (data, status) {
                    $('.data-loader').removeClass('active');
                    ctrl.result = "CARICAMENTO...OK";
                    $log.info('FUFFA DATA OK -> ' + status);
                })
                .error(function (data, status) {
                    $('.data-loader').removeClass('active');
                    ctrl.result = "ERRORE";
                    $log.warn('FUFFA NOT RETRIVED -> ' + status + " " + data.error());
                });
        };
    }]);
})();