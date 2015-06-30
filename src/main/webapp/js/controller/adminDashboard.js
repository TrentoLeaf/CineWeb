(function () {

    'use strict';

    angular.module('adminDashboard', ['constantsModule'])
        .controller ('AdminDashboardController', ['BASE', '$log', '$http', function (BASE, $log, $http) {

        var ctrl = this;
        this.result = "";

        this.fuffa = function () {
            this.result = "Caricamento...";
            $http.post(BASE + '/load-example-data')
                .success(function (data, status) {
                    ctrl.result = "Caricamento...OK";
                    $log.info('FUFFA DATA OK -> ' + status);
                })
                .error(function (data, status) {
                    ctrl.result = "Errore";
                    $log.warn('FUFFA NOT RETRIVED -> ' + status + " " + data.error());
                });
        };
    }]);
})();