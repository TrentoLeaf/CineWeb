(function () {
    'use strict';

    /* modulo per la pagina del pannello di controllo dell'admin */
    angular.module('adminDashboard', ['constantsModule'])
        .controller ('AdminDashboardController', ['BASE', '$http', function (BASE, $http) {

        var ctrl = this;
        // messaggio risultato caricamento dati di prova
        this.result = "";
        this.interval = 3;

        // carica i dati di prova impostati nel server
        this.loadSampleData = function () {

            // invia richiesta
            if (ctrl.interval >= 1) {
                $('.data-loader').addClass('active');
                this.result = "CARICAMENTO...";
                $http.post(BASE + '/load-example-data', ctrl.interval)
                    .success(function (data) { // i dati di prova sono stati caricati nel database
                        $('.data-loader').removeClass('active');
                        ctrl.result = "CARICAMENTO...OK";
                    })
                    .error(function (data) { // errore
                        $('.data-loader').removeClass('active');
                        ctrl.result = "ERRORE";
                    });
            } else {
                ctrl.result = "Inserire un valore positivo per l'intervallo tra le proiezioni";
            }
        };
    }]);
})();