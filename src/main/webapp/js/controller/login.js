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

                $('.login-loader').addClass('active');

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
                                ctrl.setAfterLogin("normal");

                                if ($rootScope.user.role == "admin") {
                                    $location.path('/admin');
                                } else {
                                    $location.path('/today');
                                }
                                break;
                            case "buy":
                                ctrl.setAfterLogin("normal");
                                $location.path('/buy');
                                break;
                            case "userArea":
                                ctrl.setAfterLogin("normal");

                                if ($rootScope.user.role == "admin") {
                                    $location.path('/admin');
                                } else {
                                    $location.path('/me');
                                }
                                break;
                        }
                        $('.login-loader').removeClass('active');
                    }).error(function (error) { /*  login failed*/
                        $rootScope.isUserLogged = false;
                        setError('Nome utente o password errati.');
                        $('.login-loader').removeClass('active');
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
                        ctrl.email = "";
                        ctrl.pass = "";
                        $location.path('/today');
                    })
                    .error( function () {
                        console.log("LOGINCONTROLLER --> LOGOUT failed");
                        // logout fallito
                        // setError("Logout fallito. Riprova.");
                        setError("");
                        $rootScope.isUserLogged = false;
                        $rootScope.user = {};
                        ctrl.email = "";
                        ctrl.pass = "";
                        $location.path('/today');
                    }
                )
            };

            this.setAfterLogin = function (type) {
                $rootScope.afterLogin = type;
            }

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

                $('.pass-rec-loader').addClass('active');

                $('#pass_recovery_message').removeClass("green-text red-text");
                $('#pass_recovery_message').addClass("white-text");
                ctrl.rec_pass_msg = "Un momento...";

                if ((this.mailForPassRecovery == undefined) || (!validateEmail(this.mailForPassRecovery))) {
                    ctrl.set_rec_pass_msg(REC_PASS_MAIL_NOT_VALID);
                    $('.pass-rec-loader').removeClass('active');
                } else {
                    Auth.forgotPassword(ctrl.mailForPassRecovery)
                        .success(function() {
                            ctrl.set_rec_pass_msg(REC_PASS_SUCCESS);
                            $('.pass-rec-loader').removeClass('active');
                        })
                        .error(function() {
                            ctrl.set_rec_pass_msg(REC_PASS_MAIL_NOT_EXIST);
                            $('.pass-rec-loader').removeClass('active');
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
