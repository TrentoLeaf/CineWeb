(function () {
    'use strict';

    angular.module('loginModule', ['usersModule', 'constantsModule'])
        .controller('LoginController', ['$rootScope', '$location', 'Auth', 'loginService', function ($rootScope, $location, Auth, loginService) {

            var ctrl = this;

            // condivisi tramite il servizio loginService
            this.user = {};
            this.logged = false;

            this.data = "";
            this.error = "";

            $rootScope.$watch(function () {
                return loginService.user;
            }, function (user) {
                ctrl.user = user;
            }, true);
            $rootScope.$watch(function () {
                return loginService.logged;
            }, function (logged) {
                ctrl.logged = logged;
            }, true);
            $rootScope.$watch(function () {
                return loginService.data;
            }, function (data) {
                ctrl.data = data;
            }, true);
            $rootScope.$watch(function () {
                return loginService.error;
            }, function (error) {
                ctrl.error = error;
            }, true);


            var setData = function (data) {
                loginService.data = data;
                loginService.error = "";
            };

            var setError = function (error) {
                loginService.data = "";
                loginService.error = error;
            };

            this.login = function (email, password) {
                //Auth.login(email, password)
                //    .success(function (user, s) {
                //        console.log(user);
                //        console.log(s);
                //    }).error(function (error, s) {
                //        console.log(error);
                //        console.log(s);
                //    });
                Auth.login(email, password).then(
                    function (data) {
                        console.log(data);

                        // set logged var
                        loginService.logged = true;
                        //save basic user data
                        loginService.user = data.data;
                        setError("");

                        // redirect alla giusta pagina
                        switch ($rootScope.afterLogin) {
                            case "normal":
                                $rootScope.afterLogin = "normal";
                                $location.path('/today');
                                break;
                            case "buy":
                                $rootScope.afterLogin = "normal";
                                $location.path('/buy');
                                break;
                            case "userArea":
                                $rootScope.afterLogin = "normal";
                                if (loginService.user.role == "admin") {
                                    $location.path('/admin/dashboard');
                                } else {
                                    $location.path('/me');
                                }
                                break;
                        }

                    },
                    function (error) {
                        console.log(error);
                        loginService.logged = false;
                        setError('Nome utente o password errati.');
                    }
                );
            };

            this.logout = function () {
                Auth.logout().then(
                    function () {
                        console.log("LOGINCONTROLLER --> LOGUOT success");
                        setData("Logout eseguito con successo.");
                        setError("");
                        loginService.logged = false;
                        loginService.user = {};
                        $location.path('/today');
                    },
                    function () {
                        console.log("LOGINCONTROLLER --> LOGUOT failed");
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
            var REC_PASS_MAIL_NOT_VALID = 2;
            var REC_PASS_REQUEST_FAIL = -1;

            this.sendPassRecoveryRequest = function () {

                $('#pass_recovery_message').removeClass("green-text red-text");
                $('#pass_recovery_message').addClass("white-text");
                ctrl.rec_pass_msg = "Un momento...";

                if ((this.mailForPassRecovery == undefined) || (!validateEmail(this.mailForPassRecovery))) {
                    ctrl.set_rec_pass_msg(REC_PASS_MAIL_NOT_VALID);
                } else {
                    // TODO invia richiesta ajax
                    // TODO attendi la risposta (successo/mail_non_registrata/fail)
                    // TODO callback:  ctrl.set_rec_pass_msg(response);
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

            /* get the info for the login controller (init) */
            this.retriveLoginData = function () {
                Auth.me().then(
                    function (data) {
                        console.log("LOGIN DATA retrived ");
                        console.log(data);

                        // set logged var
                        loginService.logged = true;
                        //save basic user data
                        loginService.user = data;
                    },
                    function () {
                        console.log("LOGIN DATA NOT retrived");
                        // set logged var
                        loginService.logged = false;
                        loginService.user = {};

                    }
                );
            };

            this.retriveLoginData();


        }])
        /* servizio per la condivisone dei dati di login tra i controller di login duplicati */
        .service('loginService', function () {
            console.log("PASSATO DI QUI");
            this.user = {};
            this.logged = false;

            this.data = "";
            this.error = "";
        });


    function validateEmail(mail) {

        var atposition = mail.indexOf("@");
        var dotposition = mail.lastIndexOf(".");
        if (atposition < 1 || dotposition < atposition + 2 || dotposition + 2 >= mail.length) {
            return false;
        } else {
            return true;
        }
    };

})();
