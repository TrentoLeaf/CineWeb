(function () {
    'use strict';

    /* modulo per la comunicazione con il server dei dati relativi all'utente */
    angular.module('usersModule', ['constantsModule', 'ngResource'])

        .factory('Users', ['BASE', '$resource', function (BASE, $resource) {
            return $resource(BASE + '/users/:id', {id: '@id'}, {
                update: {
                    method: 'PUT'
                }
            });
        }])

        .factory('Auth', ['BASE', '$http', '$q', '$log', function (BASE, $http, $q, $log) {
            var BASE_USERS = BASE + "/users";
            var BASE_BOOKINGS = BASE + "/bookings";
            var BASE_TICKETS = BASE + "/tickets";

            return {
                /* invio dati di login */
                login: function (email, password) {
                    return $http.post(BASE_USERS + '/login', {email: email, password: password})
                        .success(function (data) {
                            $log.info('LOGIN OK: ' + data);
                            $log.info(data);
                        })
                        .error(function (data, status) {
                            $log.warn('LOGIN FAILED: ' + status + " " + data.error);
                        });
                },

                /* invio richiesta di logout */
                logout: function () {
                    return $http.post(BASE_USERS + '/logout')
                        .success(function (data) {
                            $log.info('LOGOUT OK: ' + data);
                        })
                        .error(function (data, status) {
                            $log.warn('LOGOUT FAILED: ' + status + " " + data);
                        });
                },

                /* richiesta normale cambio password */
                changePassword: function (email, oldPassword, newPassword) {
                    return $http.post(BASE_USERS + '/change-password', {
                        email: email,
                        oldPassword: oldPassword,
                        newPassword: newPassword
                    })
                        .success(function (data) {
                            $log.info('CHANGE PASSWORD OK: ' + data);
                        })
                        .error(function (data, status) {
                            $log.warn('CHANGE PASSWORD FAILED: ' + status + " " + data);
                        });
                },

                /* richiesta cambio password in seguito alla richiesta di recupero della password */
                changePasswordWithCode: function (email, code, newPassword) {
                    return $http.post(BASE_USERS + '/change-password-code', {
                        email: email,
                        code: code,
                        newPassword: newPassword
                    })
                        .success(function (data) {
                            $log.info('CHANGE PASSWORD OK: ' + data);
                        })
                        .error(function (data, status) {
                            $log.warn('CHANGE PASSWORD FAILED: ' + status + " " + data);
                        });
                },

                /* richiesta di registrazione */
                registration: function (email, password, firstName, secondName) {
                    return $http.post(BASE_USERS + '/registration', {
                        email: email,
                        password: password,
                        firstName: firstName,
                        secondName: secondName
                    })
                        .success(function (data) {
                            $log.info('REGISTRATION OK: ' + data);
                        })
                        .error(function (data, status) {
                            $log.warn('REGISTRATION FAILED: ' + status + " " + data);
                        });
                },

                /* invia la conferma del click sull'url di registrazione */
                confirmRegistration: function (code) {
                    return $http.post(BASE_USERS + '/confirm', {code: code})
                        .success(function (data) {
                            $log.info('CONFIRM REGISTRATION OK: ' + data);
                        })
                        .error(function (data, status) {
                            $log.warn('CONFIRM REGISTRATION FAILED: ' + status + " " + data);
                        });
                },

                /* richiesta recupero password */
                forgotPassword: function (email) {
                    return $http.post(BASE_USERS + "/forgot-password", {email: email})
                        .success(function (data) {
                            $log.info('FORGOT PASSWORD OK: ' + data);
                        })
                        .error(function (data, status) {
                            $log.warn('FORGOT PASSWORD FAILED: ' + status + " " + data);
                        });
                },

                /* richiesta dati di base dell'utente */
                me: function () {
                    return $http.get(BASE_USERS + "/me")
                        .success(function (data) {
                            $log.info('ME OK: ' + data);
                        })
                        .error(function (data, status) {
                            $log.warn('ME FAILED: ' + status + " " + data);
                        });
                },

                /* richiesta cronologia acquisti dell'utente */
                my_bookings: function () {
                    return $http.get(BASE_BOOKINGS + "/my")
                        .success(function (data) {
                            $log.info('MY OK: ' + data);
                        })
                        .error(function (data, status) {
                            $log.warn('MY FAILED: ' + status + " " + data);
                        });
                },

                /* richiesta (admin) cronologia acquisti di un'utente*/
                user_bookings: function (uid) {
                    return $http.get(BASE_BOOKINGS + "/" + uid)
                        .success(function (data) {
                            $log.info('BOOKING_USER OK: ' + data);
                        })
                        .error(function (data, status) {
                            $log.warn('BOOKING_USER FAILED: ' + status + " " + data);
                        });
                },

                /* richiesta (admin) cancellazione biglietto */
                deleteTicket: function (tid) {
                    return $http.delete(BASE_TICKETS + "/" + tid)
                        .success(function (data) {
                            $log.info('DELETE TICKET OK: ' + data);
                        })
                        .error(function (data, status) {
                            $log.warn('DELETE TICKET FAILED: ' + status + " " + data);
                        });
                }
            }
        }]);

})();