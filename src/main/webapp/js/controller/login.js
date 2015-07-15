(function () {
    'use strict';

    /* modulo gestione login */
    angular.module('loginModule', ['usersModule', 'constantsModule'])
        .controller('LoginController', ['$rootScope', '$location', 'Auth', function ($rootScope, $location, Auth) {

            var ctrl = this;
            this.email = "";
            this.pass = "";

            // imposta messaggio errore
            var setError = function (error) {
                $rootScope.loginError = error;
            };

            // richiesta login
            this.login = function (email, password) {

                $('.login-loader').addClass('active');

                // send request and save the promise
                $rootScope.isUserLoggedPromise = Auth.login(email, password)
                    .then(function (user) {

                        setError("");
                        //save basic user data
                        $rootScope.user = user;
                        // set isUserLogged variable. All modules now know if the user is logged
                        $rootScope.isUserLogged = true;

                        // redirect all'ultima pagina visitata dall'utente o quella di default
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
                    },
                    function (error) { /* login fallito */
                        $rootScope.isUserLogged = false;
                        setError('Nome utente o password errati.');
                        $('.login-loader').removeClass('active');
                    });

                // reset campo password
                ctrl.pass = "";
            };

            // richiesta di logout
            this.logout = function () {
                Auth.logout()
                    .success(function () {
                        setError("");
                        // reset di tutte le variabili correlate
                        $rootScope.isUserLogged = false;
                        $rootScope.isUserLoggedPromise = undefined;
                        $rootScope.user = {};
                        ctrl.email = "";
                        ctrl.pass = "";
                        $location.path('/today');
                    })
                    .error(function () {
                        // logout fallito. reset variabili e redirect
                        setError("");
                        $rootScope.isUserLogged = false;
                        $rootScope.isUserLoggedPromise = undefined;
                        $rootScope.user = {};
                        ctrl.email = "";
                        ctrl.pass = "";
                        $location.path('/today');
                    }
                )
            };

            // imposta la pagina su cui tornare dopo il login
            this.setAfterLogin = function (type) {
                $rootScope.afterLogin = type;
            };

            /*
             * funzioni di redirect ad una pagina
             */
            this.losePass = function () {
                // redirect alla pagina
                $location.path('/password_recovery');
            };

            this.toUserArea = function () {
                // redirect alla pagina
                $location.path('/me');
            };

            this.toDashboard = function () {
                // redirect alla pagina
                $location.path('/admin');
            };

            this.toRegister = function () {
                // redirect alla pagina
                $location.path('/registration');
            };


            /*
             * funzioni di recupero password
             */
            this.rec_pass_msg = "";
            this.mailForPassRecovery = "";
            var REC_PASS_SUCCESS = 0;
            var REC_PASS_MAIL_NOT_EXIST = 1;
            var REC_PASS_MAIL_NOT_VALID = 2;

            // invia richiesta
            this.sendPassRecoveryRequest = function () {

                $('.pass-rec-loader').addClass('active');

                // imposta colore messaggio
                $('#pass_recovery_message').removeClass("green-text red-text");
                $('#pass_recovery_message').addClass("white-text");
                ctrl.rec_pass_msg = "Un momento...";

                if ((this.mailForPassRecovery == undefined) || (!validateEmail(this.mailForPassRecovery))) {
                    ctrl.set_rec_pass_msg(REC_PASS_MAIL_NOT_VALID);
                    $('.pass-rec-loader').removeClass('active');
                } else {
                    // esegui richiesta
                    Auth.forgotPassword(ctrl.mailForPassRecovery)
                        .success(function () {
                            ctrl.set_rec_pass_msg(REC_PASS_SUCCESS);
                            $('.pass-rec-loader').removeClass('active');
                        })
                        .error(function () {
                            ctrl.set_rec_pass_msg(REC_PASS_MAIL_NOT_EXIST);
                            $('.pass-rec-loader').removeClass('active');
                        });
                }
            };

            //setta l'errore o il messaggio del risultato della richiesta di recupero della password
            this.set_rec_pass_msg = function (result) {

                $('#pass_recovery_message').removeClass("white-text");

                if (result == REC_PASS_SUCCESS) { // ok
                    $('#pass_recovery_message').addClass("green-text");
                    ctrl.rec_pass_msg = "Richiesta effettuata. Controlla la tua casella di posta.";
                } else if (result == REC_PASS_MAIL_NOT_EXIST) { // mail inesistente
                    $('#pass_recovery_message').addClass("red-text");
                    ctrl.rec_pass_msg = "L'indirizzo mail inserito non risulta registrato. Riprova.";
                } else if (result == REC_PASS_MAIL_NOT_VALID) { // mail non valida
                    $('#pass_recovery_message').addClass("red-text");
                    ctrl.rec_pass_msg = "L'indirizzo mail inserito non è corretto. Riprova.";
                } else {    // errore richiesta
                    $('#pass_recovery_message').addClass("red-text");
                    ctrl.rec_pass_msg = "La richiesta non è andata a buon fine. Riprova.";
                }
            };
        }]);


    // validazione di una mail
    function validateEmail(mail) {

        var atposition = mail.indexOf("@");
        var dotposition = mail.lastIndexOf(".");
        if (atposition < 1 || dotposition < atposition + 2 || dotposition + 2 >= mail.length) {
            return false;
        }
        return true;
    }

})();
