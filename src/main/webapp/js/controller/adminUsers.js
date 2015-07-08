(function () {
    'use strict';

    angular.module('adminUsers', ['usersModule'])

        .controller('AdminUsersEditController', ['$routeParams', '$location', 'Users', function($routeParams, $location, Users) {

            var c = this;
            this.currentUser = {};
            this.status = "";
            this.error_message = false;

            Users.get({id:$routeParams.uid}).$promise.then(function (data) {
                c.currentUser = data;
            }, function () {
                $location.path("/admin/users");
            });


            this.save = function () {
                Users.update({id: c.currentUser.uid}, c.currentUser).$promise.then(function (data) {
                    // ok
                    console.log("UPDATE OK ->");
                    console.log(data);
                    $location.path("/admin/users")
                }, function (response) {
                    if (response.status == 409) {
                        ctrl.error_message = true;
                        ctrl.setStatus("Email già in uso");
                    } else if (response.status == 400) {
                        ctrl.error_message = true;
                        ctrl.setStatus("I dati inseriti non sono validi");
                    } else {
                        ctrl.error_message = true;
                        ctrl.setStatus("Qualcosa è andato storto, riprova");
                    }
                })
            };
            this.setStatus = function (status) {
                ctrl.setStatusClass();
                ctrl.status = status;
            };

            this.setStatusClass = function () {
                if(ctrl.error_message) {
                    $('#user_edit_message').removeClass("green-text white-text");
                    $('#user_edit_message').addClass("red-text");
                } else {
                    $('#user_edit_message').removeClass("red-text white-text");
                    $('#user_edit_message').addClass("green-text");
                }
            };
        }])

        .controller('AdminUserBookingsController', ['$routeParams', '$scope', '$location', '$anchorScroll', 'Users', 'Auth', function($routeParams, $scope, $location, $anchorScroll, Users, Auth) {

            var ctrl = this;
            this.bookings = [];
            this.uid = $routeParams.uid;
            this.currentTid = -1;
            this.status_msg = "";

            // convert 0 to A, 1 to B, ...
            this.intToChar = function (i) {
                return String.fromCharCode('A'.charCodeAt() + parseInt(i));
            };

            this.getBookings = function () {
                Auth.user_bookings($routeParams.uid)
                    .success(function (data) {
                        ctrl.bookings = data;

                        // calculate total of every buy
                        for (var i=0; i < ctrl.bookings.length; i++) {
                            var buy = ctrl.bookings[i];
                            var total = 0;
                            for (var j=0; j < buy.tickets.length; j++) {
                                if (buy.tickets[j].price != undefined) {
                                    total += buy.tickets[j].price;
                                }
                            }
                            buy.total = total;
                        }

                    })
                    .error(function () {
                    });

            };

            this.modifyTicketStatus = function () {

                ctrl.status_msg = "Un momento...";

                Auth.deleteTicket(ctrl.currentTid)
                    .success(function () {
                        ctrl.status_msg = "Modifica eseguita.";
                        // update bookings
                        ctrl.getBookings();
                        $anchorScroll();
                    })
                    .error(function () {
                        ctrl.status_msg = "Modifica fallita.";
                        // update bookings
                        ctrl.getBookings();
                        $anchorScroll();
                    });

                ctrl.close_delete_modal();
            };

            this.open_modal_confirm = function (booking_index, ticket_index) {
                if (! ctrl.bookings[booking_index].tickets[ticket_index].deleted) {
                    // set tid
                    ctrl.currentTid = ctrl.bookings[booking_index].tickets[ticket_index].tid;

                    $('#modal_delete_ticket_confirm').openModal();
                }
            };

            this.close_delete_modal = function () {
                $('#modal_delete_ticket_confirm').closeModal();
            };

            // init collapsible
            $scope.$on('collapsibleRepeatEnd', function(scope, element, attrs){
                $('.collapsible').collapsible({
                    accordion : false // A setting that changes the collapsible behavior to expandable instead of the default accordion style
                });
                console.log("collapsible INIZIALIZZATI");
            });


            ctrl.getBookings();
        }])

        .controller('AdminUsersController', ['$rootScope', '$location', 'Users', function ($rootScope, $location, Users) {


            var ctrl = this;
            this.order = 'uid';
            this.reverse = false;

            this.status = "";
            this.error_message = false;

            this.newUser = new Users();
            this.newUser.enabled = true;
            this.verifyPassword = "";
            this.newUserRole = false;
            this.currentUser = {};

            this.tmpUser = {};

            var init = function () {
                if ($rootScope.isUserLogged == false) {
                    $rootScope.afterLogin = "userArea";
                    $location.path('/login');
                }

                ctrl.loading = true;
                ctrl.users = [];
            };

            // order list
            this.setOrder = function (order) {
                if (this.order === order) {
                    this.reverse = !this.reverse;
                } else {
                    this.reverse = false;
                    this.order = order;
                }
            };

            // load the users list
            this.loadUsers = function () {
                init();
                Users.query(function (data) {
                    ctrl.users = data;
                    ctrl.loading = false;
                });
            };

            // remove a user
            this.deleteUser = function (user) {
                // better BEFORE calling this function
                Users.delete({id: user.uid}, function () {
                    // ok
                    ctrl.users.splice(ctrl.users.indexOf(user), 1);
                    ctrl.error_message = false;
                    ctrl.setStatus("Utente cancellato con successo");
                    $("html, body").animate({ scrollTop: 0 }, "fast");
                }, function (response) {
                    // fail
                    if (response.status == 404) {
                        ctrl.error_message = true;
                        ctrl.setStatus("Impossibile procedere alla cancellazione, utente non trovato");
                    } else {
                        ctrl.error_message = true;
                        ctrl.setStatus("Qualcosa è andato storto, riprova");
                    }
                    $("html, body").animate({ scrollTop: 0 }, "fast");
                });
            };

            // save the current user
            this.addUser = function () {
                if (ctrl.verifyPassword == ctrl.newUser.password) {
                    if (ctrl.newUserRole) {
                        ctrl.newUser.role = "admin";
                    } else {
                        ctrl.newUser.role = "client";
                    }

                    ctrl.newUser.$save(function (data) {
                        ctrl.users.push(data);
                        ctrl.newUser = new Users();
                        ctrl.newUser.enabled = true;
                        console.log("Insertion succes");
                        console.log(data);
                        $location.path("/admin/users");
                    }, function(response) {
                        if (response.status == 409) {
                            ctrl.error_message = true;
                            ctrl.setStatus("Email già in uso");
                        } else if (response.status == 400) {
                            ctrl.error_message = true;
                            ctrl.setStatus("Controlla i dati inseriti");
                        } else {
                            ctrl.error_message = true;
                            ctrl.setStatus("Qualcosa è andato storto, riprova");
                        }
                        $("html, body").animate({ scrollTop: 0 }, "fast");
                    });
                } else {
                    ctrl.newUser.password = "";
                    ctrl.verifyPassword = "";
                    this.open_wrong_password_modal();
                    $('#password').addClass("invalid");
                    $('#verifyPassword').addClass("invalid");
                }
            };

            // load data at start
            this.loadUsers();

            this.open_user_delete_modal = function (index) {
                this.tmpUser = this.users[index];
                $('#modal_user_delete').openModal();
            };

            this.close_user_delete_modal = function () {
                $('#modal_user_delete').closeModal();
            };

            this.open_wrong_password_modal = function () {
                $('#modal_wrong_password').openModal();
            };

            this.close_wrong_password_modal = function () {
                $('#modal_wrong_password').closeModal();
                $('#password').focus();
            };

            this.setStatus = function (status) {
                ctrl.setStatusClass();
                ctrl.status = status;
            };

            this.setStatusClass = function () {
                if(ctrl.error_message) {
                    $('#user_message').removeClass("green-text white-text");
                    $('#user_message').addClass("red-text");
                } else {
                    $('#user_message').removeClass("red-text white-text");
                    $('#user_message').addClass("green-text");
                }
            };
        }]);

})();
