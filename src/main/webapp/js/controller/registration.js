(function () {
    'use strict';

    angular.module('registrationModule', ['usersModule'])
        .controller('RegistrationCtrl', ['Auth', function (Auth) {

            console.log("yeeeeeeeeeeee");

            var ctrl = this;

            this.status = "";
            this.user = {};

            var setStatus = function (msg) {
                ctrl.status = msg;
            };

            this.submit = function (user) {
                console.log(user);

                Auth.registration(user.email, user.password, user.firstName, user.secondName).then(
                    function (data) {
                        setStatus("OK " + data);
                    },
                    function (error) {
                        setStatus("ERROR " + error);
                    }
                );
            };
        }]);

})();