(function () {
    'use strict';

    angular.module('storageModule', ['angular-locker'])
        .factory('StorageService', ['locker', function (locker) {

            var EMAIL = 'email';
            var NAME = 'name';
            var ROLE = 'role';

            return {

                login: function (payload) {
                    locker.put(EMAIL, payload.email);
                    locker.put(NAME, payload.name);
                    locker.put(ROLE, payload.role);
                },

                logout: function () {
                    locker.forget(EMAIL);
                    locker.forget(NAME);
                    locker.forget(ROLE);
                },

                getEmail: function () {
                    return locker.get(EMAIL);
                },

                getName: function () {
                    return locker.get(NAME);
                },

                getRole: function () {
                    return locker.get(ROLE);
                },

                setEmail: function (email) {
                    locker.put(EMAIL, email);
                },

                setName: function (name) {
                    locker.put(NAME, name);
                }

            }
        }]);
})();