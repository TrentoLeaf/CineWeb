(function () {
    'use strict';

    /* modulo per la pagina di registrazione*/
    angular.module('registrationModule', ['usersModule'])
        .controller('RegistrationCtrl', ['Auth', function (Auth) {

            var ctrl = this;

            this.status = "";
            this.user = {};
            this.verifyPassword = "";

            // imposta messaggio
            var setStatus = function (msg) {
                ctrl.status = msg;
            };

            // invia richiesta registrazione
            this.submit = function (user) {

                $('.registration-loader').addClass('active');

                // imposta colore messaggio
                $('#registration_message').removeClass("red-text green-text");
                $('#registration_message').addClass("white-text");
                // messaggio
                setStatus("Un momento...");

                if (ctrl.verifyPassword == ctrl.user.password) {
                    // esegui richiesta
                    Auth.registration(user.email, user.password, user.firstName, user.secondName)
                        .success(
                        function (data) {
                            $('#registration_message').removeClass("red-text white-text");
                            $('#registration_message').addClass("green-text");
                            setStatus("La richiesta è stata inoltrata. Controlla la tua casella di posta.");
                            $('.registration-loader').removeClass('active');
                        })
                        .error(
                        function (error, status) {
                            $('#registration_message').removeClass("green-text white-text");
                            $('#registration_message').addClass("red-text");
                            if (status == 409) {
                                setStatus("La mail è già stata utilizzata. Si prega di cambiarla.");
                            } else {
                                setStatus("La richiesta non è andata a buon fine. Ricontrolla i dati inseriti.");
                            }
                            $('.registration-loader').removeClass('active');
                        }
                    );
                } else { // la password di verifica non coincide
                    $('#registration_message').removeClass("green-text white-text");
                    $('#registration_message').addClass("red-text");
                    ctrl.user.password = "";
                    ctrl.verifyPassword = "";
                    $('#password').addClass("invalid");
                    $('#password').focus();
                    $('#verifyPassword').addClass("invalid");
                    setStatus("Le password inserite non coincidono, riprova.");
                    $('.registration-loader').removeClass('active');
                }
            };
        }]);

})();