(function () {
    'use strict';

    angular.module('registrationModule', ['usersModule'])
        .controller('RegistrationCtrl', ['Auth', function (Auth) {

            var ctrl = this;

            this.status = "";
            this.user = {};
            this.verifyPassword = "";

            var setStatus = function (msg) {
                ctrl.status = msg;
            };

            this.submit = function (user) {

                $('.registration-loader').addClass('active');

                $('#registration_message').removeClass("red-text green-text");
                $('#registration_message').addClass("white-text");
                setStatus("Un momento...");

                if (ctrl.verifyPassword == ctrl.user.password) {
                    Auth.registration(user.email, user.password, user.firstName, user.secondName)
                        .success(
                        function (data) {
                            $('#registration_message').removeClass("red-text white-text");
                            $('#registration_message').addClass("green-text");
                            setStatus("La richiesta è stata inoltrata. Controlla la tua casella di posta.");
                            console.log(data);
                            $('.registration-loader').removeClass('active');
                        })
                        .error(
                        function (error) {
                            $('#registration_message').removeClass("green-text white-text");
                            $('#registration_message').addClass("red-text");
                            if (error.error[0] == ".") {
                                setStatus("La mail è già stata utilizzata. Si prega di cambiarla.");
                            } else {
                                setStatus("La richiesta non è andata a buon fine. Ricontrolla i dati inseriti.");
                            }
                            console.log(error);
                            $('.registration-loader').removeClass('active');
                        }
                    );
                } else {
                    $('#registration_message').removeClass("green-text white-text");
                    $('#registration_message').addClass("red-text");
                    ctrl.user.password = "";
                    ctrl.verifyPassword = "";
                    $('#password').addClass("invalid");
                    $('#password').focus();
                    $('#verifyPassword').addClass("invalid");
                    setStatus("Le password inserite non combaciano, riprova.");
                    $('.registration-loader').removeClass('active');
                };
            };
        }]);

})();