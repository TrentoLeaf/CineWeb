(function () {
    'use strict';

    var app = angular.module('loginModule', ['usersModule', 'storageModule', 'constantsModule']);

    app.controller('LoginController', ['$location', 'Auth', 'StorageService', function ($location, Auth, StorageService) {

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
                    setData(data);
                    StorageService.login(data);
                    // set logged var
                    ctrl.logged = true;
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
        };

        this.toUserArea = function () {
            $location.path('/userArea');
        }

        this.register = function (email) {
            // redirect al partial registrazione
            $location.path('/registration');
        };


    }]);

})();
