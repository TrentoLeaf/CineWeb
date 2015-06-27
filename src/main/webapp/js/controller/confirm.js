(function () {
    'use strict';

    angular.module('confirmModule', ['usersModule'])
        .controller('ConfirmController', ['$location', 'Auth', function ($location, Auth) {

            var ctrl = this;
            this.loading = true;
            this.status = "";

            this.beforeHtml = "<div class='row'></div><div class='row center-align white-text'><h5>";
            this.afterHtml = "</h5></div>";
            var confirm = function () {

                // get and remove c
                var code = $location.search().c;
                //$location.search('c', null);

                // do confirm request...
                if (code == undefined) {
                    ctrl.loading = false;
                    ctrl.status = this.beforeHtml+"Link non più valido o già usato."+this.afterHtml;
                } else {
                    Auth.confirmRegistration(code).then(
                        function () {
                            ctrl.loading = false;
                            ctrl.status = this.beforeHtml+"Account confermato. Ora puoi acquistare biglietti!"+this.afterHtml;
                        },
                        function () {
                            ctrl.loading = false;
                            ctrl.status = this.beforeHtml+"Link non più valido o già usato."+this.afterHtml;
                        }
                    );
                }
            };

            // when page loads
            confirm();

        }]);
})();