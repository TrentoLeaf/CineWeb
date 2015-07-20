(function () {
    'use strict';

    /* modulo per la conferma di una registrazione */
    angular.module('confirmModule', ['usersModule'])
        .controller('ConfirmController', ['$location', 'Auth', function ($location, Auth) {

            var ctrl = this;
            this.loading = true;
            this.status = "";

            // invio conferma
            var confirm = function () {

                // recupero codice di conferma dall'url
                var code = $location.search().c;

                if (code == undefined) {
                    ctrl.loading = false;
                    ctrl.status = "Link non più valido o già usato.";
                } else {
                    // esegui invio conferma
                    Auth.confirmRegistration(code).then(
                        function () {
                            ctrl.loading = false;
                            ctrl.status = "Account confermato. Ora puoi acquistare biglietti!";
                        },
                        function () {
                            ctrl.loading = false;
                            ctrl.status = "Link non più valido o già usato.";
                        }
                    );
                }
            };

            // invia conferma al server appena la pagina è stata caricata
            confirm();

        }]);
})();