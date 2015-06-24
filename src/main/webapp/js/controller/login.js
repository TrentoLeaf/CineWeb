(function () {
    'use strict';

    angular.module('loginModule', ['usersModule', 'constantsModule'])
        .controller('LoginController', ['$rootScope', '$location', 'Auth', function ($rootScope, $location, Auth) {

            var ctrl = this;
            this.email = "";
            this.pass = "";

            var setError = function (error) {
                $rootScope.loginError = error;
            };

            this.login = function (email, password) {
                Auth.login(email, password)
                    .success(function (user) {

                        // set isUserLogged var
                        $rootScope.isUserLogged = true;
                        //save basic user data
                        $rootScope.user = user;
                        setError("");

                        // redirect all' ultima pagina
                        switch ($rootScope.afterLogin) {
                            case "normal":
                                $rootScope.afterLogin = "normal";

                                if ($rootScope.user.role == "admin") {
                                    $location.path('/admin');
                                } else {
                                    $location.path('/today');
                                }
                                break;
                            case "buy":
                                $rootScope.afterLogin = "normal";
                                $location.path('/buy');
                                break;
                            case "userArea":
                                $rootScope.afterLogin = "normal";

                                if ($rootScope.user.role == "admin") {
                                    console.log("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa");
                                    $location.path('/admin');
                                } else {
                                    $location.path('/me');
                                }
                                break;
                        }
                    }).error(function (error) { /*  login failed*/
                        $rootScope.isUserLogged = false;
                        setError('Nome utente o password errati.');
                    });

                ctrl.pass = "";
            };

            this.logout = function () {
                Auth.logout()
                    .success( function () {
                        console.log("LOGINCONTROLLER --> LOGOUT success");
                        setError("");
                        $rootScope.isUserLogged = false;
                        $rootScope.user = {};
                        $location.path('/today');

                        ctrl.email = "";
                        ctrl.pass = "";
                    })
                    .error( function () {
                        console.log("LOGINCONTROLLER --> LOGOUT failed");
                        setError("Logout fallito. Riprova.");
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

            this.losePass = function () {
                // redirect to a new partial
                $location.path('/password_recovery');
            };

            this.toUserArea = function () {
                $location.path('/me');
            };

            this.toDashboard = function () {
                $location.path('/admin');
            };

            this.toRegister = function () {
                // redirect al partial registrazione
                $location.path('/registration');
            };


            /* recovery password functions */

            this.rec_pass_msg = "";
            this.mailForPassRecovery = "";
            var REC_PASS_SUCCESS = 0;
            var REC_PASS_MAIL_NOT_EXIST = 1;
            var REC_PASS_MAIL_NOT_VALID = 2;

            this.sendPassRecoveryRequest = function () {

                $('#pass_recovery_message').removeClass("green-text red-text");
                $('#pass_recovery_message').addClass("white-text");
                ctrl.rec_pass_msg = "Un momento...";

                if ((this.mailForPassRecovery == undefined) || (!validateEmail(this.mailForPassRecovery))) {
                    ctrl.set_rec_pass_msg(REC_PASS_MAIL_NOT_VALID);
                } else {
                    Auth.forgotPassword(ctrl.mailForPassRecovery)
                        .success(function() {
                            ctrl.set_rec_pass_msg(REC_PASS_SUCCESS);
                        })
                        .error(function() {
                            ctrl.set_rec_pass_msg(REC_PASS_MAIL_NOT_EXIST);
                        });
                }
            };

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
            };
        }]);


    function validateEmail(mail) {

        var atposition = mail.indexOf("@");
        var dotposition = mail.lastIndexOf(".");
        if (atposition < 1 || dotposition < atposition + 2 || dotposition + 2 >= mail.length) {
            return false;
        }
        return true;
    }

})();
