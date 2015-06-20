(function () {
    'use strict';

    angular.module('loginModule', ['usersModule', 'storageModule', 'constantsModule'])
        .controller('LoginController', ['$location', 'Auth', 'StorageService', function ($location, Auth, StorageService) {

            var ctrl = this;

            this.user = {};
            this.logged = false;

            this.data = "";
            this.error = "";

            this.email = StorageService.getEmail();

            var setData = function (data) {
                ctrl.data = data;
                ctrl.error = "";
            };

            var setError = function (error) {
                ctrl.data = "";
                ctrl.error = error;
            };

            this.login = function (email, password) {
                Auth.login(email, password).then(
                    function (data) {
                        StorageService.login(data);
                        // set logged var
                        ctrl.logged = true;
                        setError("");
                        // redirect al partial principale
                        $location.path('/today');

                    },
                    function (error) {
                        ctrl.logged = false;
                        setError('Nome utente o password errati.');
                    }
                );
            };

            this.logout = function () {
                Auth.logout().then(
                    function () {
                        setData("Logout eseguito con successo.");
                        setError("");
                        StorageService.logout();
                        ctrl.logged = false;
                        $location.path('/today');
                    },
                    function () {
                        setError("Logout fallito. Riprova.");
                        StorageService.logout();
                    }
                )
            };

            this.change = function (email, oldPass, newPass) {
                Auth.changePassword(email, oldPass, newPass).then(
                    function () {
                        setData("pass OK");
                    },
                    function () {
                        setError("pass ERROR");
                    }
                )
            };

            this.losePass = function (email) {
                // redirect to a new partial
                $location.path('/password_recovery');
            };

            this.toUserArea = function () {
                $location.path('/me');
            };

            this.register = function (email) {
                // redirect al partial registrazione
                $location.path('/registration');
            };


            /* recovery password functions */

            this.rec_pass_msg = "";
            this.mailForPassRecovery = "";
            var REC_PASS_SUCCESS = 0;
            var REC_PASS_MAIL_NOT_EXIST = 1;
            var REC_PASS_MAIL_NOT_VALID = 2
            var REC_PASS_REQUEST_FAIL = -1;

            this.sendPassRecoveryRequest = function () {

                $('#pass_recovery_message').removeClass("green-text red-text");
                $('#pass_recovery_message').addClass("white-text");
                ctrl.rec_pass_msg = "Un momento...";

                if ((this.mailForPassRecovery == undefined) || (! validateEmail(this.mailForPassRecovery))) {
                    ctrl.set_rec_pass_msg(REC_PASS_MAIL_NOT_VALID);
                } else {
                    // TODO invia richiesta ajax
                    // TODO attendi la risposta (successo/mail_non_registrata/fail)
                    // TODO callback:  ctrl.set_rec_pass_msg(response);
                }
            }

            /* setta l'errore o il messaggio per il recupero della password */
            this.set_rec_pass_msg = function (result) {

                $('#pass_recovery_message').removeClass("white-text");
                if (result == REC_PASS_SUCCESS) { // ok
                    $('#pass_recovery_message').addClass("green-text");
                    ctrl.rec_pass_msg = "Richiesta effettuata. Controlla la tua casella di posta.";
                } else if (result == REC_PASS_MAIL_NOT_EXIST) { // mail inexistent
                    $('#pass_recovery_message').addClass("red-text");
                    ctrl.rec_pass_msg = "L'indirizzo mail inserito non risulta registrato. Riprova.";
                } else if (result == REC_PASS_MAIL_NOT_VALID) {
                    $('#pass_recovery_message').addClass("red-text");
                    ctrl.rec_pass_msg = "L'indirizzo mail inserito non è corretto. Riprova.";
                } else {    // request error
                    $('#pass_recovery_message').addClass("red-text");
                    ctrl.rec_pass_msg = "La richiesta non è andata a buon fine. Riprova.";
                }

            }




        }]);

})();


function validateEmail(mail) {

    var atposition=mail.indexOf("@");
    var dotposition=mail.lastIndexOf(".");
    if (atposition<1 || dotposition<atposition+2 || dotposition+2>=mail.length){
        return false;
    } else {
        return true;
    }
}

