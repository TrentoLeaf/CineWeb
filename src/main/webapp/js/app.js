(function () {
    'use strict';

    $(document).ready(function () {

        // init the mobile menu sidenav
        $('.button-collapse').sideNav({
            closeOnClick: true
        });

        // init the dropdown selectors
        $('select').material_select();

        // apre i modal di materialize
        $('.modal-trigger').leanModal({
            dismissible: true, // Modal can be dismissed by clicking outside of the modal
            opacity: .5, // Opacity of modal background
            in_duration: 400, // Transition in duration
            out_duration: 300 // Transition out duration
        });

        // init pulsante fixed-button admin
        var button = $('#fab_primary');
        var icon = $('.fixed-button a i');

        button.click(function () {
            if(button.hasClass("active")) {
                button.toggleClass("active");
                $(".fixed-list").removeClass("slideInUp");
                $(".fixed-list").addClass("slideOutDown");
                icon.removeClass("rotateOut");
                icon.addClass("rotateIn");
            } else {
                $(".fixed-list").removeClass("hide-list");
                button.toggleClass("active");
                $(".fixed-list").removeClass("slideOutDown");
                $(".fixed-list").addClass("slideInUp");
                icon.removeClass("rotateIn");
                icon.addClass("rotateOut");
            }
        });
    });
})();

(function () {
    'use strict';

    /* modulo principale dell'applicazione con relative dipendenze */
    angular.module('cineweb', ['ngRoute', 'uiGmapgoogle-maps', 'cartModule', 'PlaysModule', 'tabmodule', 'loginModule', 'roomsModule',
        'registrationModule', 'meModule', 'adminDashboard', 'adminUsers', 'adminFilms', 'adminPrices', 'adminPlays', 'adminRooms', 'adminStats', 'confirmModule', 'resetModule', 'buyModule', 'pricesModule', 'mapModule', 'buyProcedureModule'])


        /* routing e navigazione nelle pagine del sito */
        .config(['$routeProvider', function ($routeProvider) {

            /* routing of the site */
            $routeProvider.when('/', {
                redirectTo: '/today'
            }).when('/home', {
                redirectTo: '/today'
            }).when('/today', {
                templateUrl: '../partials/today.html',
                controller: 'PlaysController',
                controllerAs: 'ctrl'
            }).when('/today_mobile', {
                templateUrl: '../partials/today_mobile.html',
                controller: 'PlaysController',
                controllerAs: 'ctrl'
            }).when('/info', {
                templateUrl: '../partials/info.html'
            }).when('/cart_mobile', {
                templateUrl: '../partials/cart_mobile.html',
                controller: 'CartController',
                controllerAs: 'cc'
            }).when('/login_mobile', {
                templateUrl: '../partials/login_mobile.html',
                controller: 'LoginController',
                controllerAs: 'c'
            }).when('/registration', {
                templateUrl: '../partials/registration.html',
                controller: 'RegistrationCtrl',
                controllerAs: 'ctrl'
            }).when('/password_recovery', {
                templateUrl: '../partials/password_recovery.html',
                controller: 'LoginController',
                controllerAs: 'c'
            }).when('/me', {
                templateUrl: '../partials/me.html',
                controller: 'MeController',
                controllerAs: 'ctrl',
                resolve: { factory: checkRouting }
            }).when('/login', {
                templateUrl: '../partials/login.html',
                controller: 'LoginController',
                controllerAs: 'c'
            }).when('/confirm', {
                templateUrl: '../partials/confirm.html',
                controller: 'ConfirmController',
                controllerAs: 'ctrl'
            }).when('/reset', {
                templateUrl: '../partials/reset.html',
                controller: 'ResetController',
                controllerAs: 'ctrl'
            }).when('/admin', {
                templateUrl: '../partials/admin/dashboard.html',
                controller: 'AdminDashboardController',
                controllerAs: 'ctrl',
                resolve: { factory: checkRouting }
            }).when('/admin/films', {
                templateUrl: '../partials/admin/films.html',
                controller: 'AdminFilmsController',
                controllerAs: 'ctrl',
                resolve: { factory: checkRouting }
            }).when('/admin/films/new', {
                templateUrl: '../partials/admin/new_film.html',
                controller: 'AdminFilmsController',
                controllerAs: 'ctrl',
                resolve: { factory: checkRouting }
            }).when('/admin/films/:fid', {
                templateUrl: '../partials/admin/edit_film.html',
                controller: 'AdminFilmsEditController',
                controllerAs: 'ctrl',
                resolve: { factory: checkRouting }
            }).when('/admin/plays', {
                templateUrl: '../partials/admin/plays.html',
                controller: 'AdminPlaysController',
                controllerAs: 'ctrl',
                resolve: { factory: checkRouting }
            }).when('/admin/plays/new', {
                templateUrl: '../partials/admin/new_play.html',
                controller: 'AdminNewPlaysController',
                controllerAs: 'ctrl',
                resolve: { factory: checkRouting }
            }).when('/admin/plays/:pid', {
                templateUrl: '../partials/admin/edit_play.html',
                controller: 'AdminPlaysEditController',
                controllerAs: 'ctrl',
                resolve: { factory: checkRouting }
            }).when('/admin/prices', {
                templateUrl: '../partials/admin/prices.html',
                controller: 'AdminPricesController',
                controllerAs: 'ctrl',
                resolve: { factory: checkRouting }
            }).when('/admin/rooms', {
                templateUrl: '../partials/admin/rooms.html',
                controller: 'AdminRoomsController',
                controllerAs: 'ctrl',
                resolve: { factory: checkRouting }
            }).when('/admin/rooms/new_room', {
                templateUrl: '../partials/admin/new_room.html',
                controller: 'AdminNewRoomController',
                controllerAs: 'ctrl',
                resolve: { factory: checkRouting }
            }).when('/admin/rooms/edit_room/:rid', {
                templateUrl: '../partials/admin/edit_room.html',
                controller: 'AdminEditRoomController',
                controllerAs: 'ctrl',
                resolve: { factory: checkRouting }
            }).when('/admin/stats', {
                templateUrl: '../partials/admin/stats.html',
                controller: 'AdminStatsController',
                controllerAs: 'ctrl',
                resolve: { factory: checkRouting }
            }).when('/admin/users', {
                templateUrl: '../partials/admin/users.html',
                controller: 'AdminUsersController',
                controllerAs: 'ctrl',
                resolve: { factory: checkRouting }
            }).when('/admin/users/new', {
                templateUrl: '../partials/admin/new_user.html',
                controller: 'AdminUsersController',
                controllerAs: 'ctrl',
                resolve: { factory: checkRouting }
            }).when('/admin/users/:uid', {
                templateUrl: '../partials/admin/edit_user.html',
                controller: 'AdminUsersEditController',
                controllerAs: 'ctrl',
                resolve: { factory: checkRouting }
            }).when('/admin/users/bookings/:uid', {
                templateUrl: '../partials/admin/user_bookings.html',
                controller: 'AdminUserBookingsController',
                controllerAs: 'ctrl',
                resolve: { factory: checkRouting }
            }).when('/buy', {
                templateUrl: '../partials/buy_seats.html',
                controller: 'BuySeatController',
                controllerAs: 'ctrl'
            }).when('/buy_start_error', {
                templateUrl: '../partials/buy_seats_error.html'
            }).when('/buy_last_step', {
                templateUrl: '../partials/buy_summary.html',
                controller: 'BuySummaryController',
                controllerAs: 'ctrl'
            }).when('/buy_complete', {
                templateUrl: '../partials/buy_complete.html',
                controller: 'BuyCompleteController',
                controllerAs: 'ctrl'
            }).when('/error', {
                templateUrl: '../partials/error.html'
            }).when('/popcorn', {
                templateUrl: '../partials/popcorn.html'
            }).otherwise({
                redirectTo: '/error'
            });
        }])

        .config(['uiGmapGoogleMapApiProvider', function (uiGmapGoogleMapApiProvider) {
            uiGmapGoogleMapApiProvider.configure({
                //    key: 'your api key',
                v: '3.17',
                libraries: 'weather,geometry,visualization'
            });
        }])

        // direttiva per la visualizzazione dell'animazione di caricamento
        .directive('loading', function () {
            return {
                restrict: 'E',
                templateUrl: '../partials/directives/loading.html',
                scope: {
                    loading: '=loading'
                }
            }
        })

        // direttiva per inizializzare i dropdown
        .directive('onDropdownRepeat', function () {
            return function (scope, element, attrs) {
                if (scope.$last) {
                    scope.$emit('dropdownRepeatEnd', element, attrs);
                }
            };
        })

        // direttiva per inizializzare i collapsible
        .directive('onCollapsibleRepeat', function () {
            return function (scope, element, attrs) {
                if (scope.$last) {
                    scope.$emit('collapsibleRepeatEnd', element, attrs);
                }
            };
        })

        // direttiva per inizializzare i select
        .directive('onSelectRepeat', function () {
            return function (scope, element, attrs) {
                if (scope.$last) {
                    scope.$emit('selectRepeatEnd', element, attrs);
                }
            };
        })

        // direttiva per inizializzare i tooltip
        .directive('onTooltipRepeat', function () {
            return function (scope, element, attrs) {
                if (scope.$last) {
                    scope.$emit('tooltipRepeatEnd', element, attrs);
                }
            };
        })

        // init all'avvio applicazione
        .run(['$rootScope', '$location', '$anchorScroll', '$q', 'Prices', 'StorageService', 'Auth', 'CompletePlays', '$sce', 'BuyProcedure', function ($rootScope, $location, $anchorScroll, $q, Prices, StorageService, Auth, CompletePlays, $sce, BuyProcedure) {

            // redirect only if needed
            var redirect = function (path) {
                if ($location.path() != path) {
                    $location.path(path);
                }
            };

            // routing manipulation
            $rootScope.$on('$routeChangeStart', function (event, next) {

                // check for a c parameter
                var c = $location.search().c;

                // confirm page
                if (c != undefined) {
                    redirect('/confirm');
                }

                // check for a r parameter
                var r = $location.search().r;

                if (r != undefined) {
                    redirect('/reset');
                }

            });


            /* set listener for route change auto sroll to up */
            $rootScope.$on("$routeChangeSuccess", function(){
                $anchorScroll();
            });

            /* utils */
            // copia un oggetto e ritorna la copia
            $rootScope.cloneObject = function (obj) {
                return (JSON.parse(JSON.stringify(obj)));
            };

            // manipulation and trusting (enabling cross-origin resources) of trailers url
            $rootScope.trustSrcTrailerUrl = function (src) {
                if (src != undefined) {
                    src = src.replace("watch?v=", "embed/");
                }
                return $sce.trustAsResourceUrl(src);
            };

            //updateTotal
            /*
             * aggiorna il totale controllando per tutti il film nel carrello,
             * il numero e tipo di biglietti e li moltiplica per il  loro prezzo
             */
            $rootScope.updateTotal = function () {
                $rootScope.total = 0;

                for (var i = 0; i < $rootScope.cart.length; i++) {
                    for (var j = 0; j < $rootScope.tickets.length; j++) {
                        var num = 0;
                        for (var k = 0; k < $rootScope.cart[i].tickets.length; k++) {
                            if ($rootScope.cart[i].tickets[k].type == $rootScope.tickets[j].type) {
                                num = num + $rootScope.cart[i].tickets[k].number;
                            }
                        }
                        $rootScope.total = $rootScope.total + ($rootScope.tickets[j].price * num);
                    }
                }
            };


            /* init of login data */
            $rootScope.user = {}; // dati untente di base
            $rootScope.isUserLogged = false;
            $rootScope.isUserLoggedPromise = Auth.me();  // request to server the data of an user and check if the user is logged
            $rootScope.loginError = "";
            $rootScope.afterLogin = "normal"; // variabile per sapere dove redirigere dopo un login (normal, buy, userArea)

            // request to server the data of a logged user. If the user isn't logged set the login variables. Return the promise of the request
            var retrieveUserData = function () {
                $rootScope.isUserLoggedPromise.then(
                    function (data) {
                        // user is already logged
                        $rootScope.isUserLogged = true;
                        //save basic user data
                        $rootScope.user = data;
                    }, function (data) {
                        // not logged
                        $rootScope.isUserLogged = false;
                        $rootScope.user = {};
                    });
            };

            retrieveUserData();


            /* init of prices */

            // function to load the prices
            $rootScope.loadPrices = function () {
                Prices.getList().then(function (result) {
                    $rootScope.tickets = result.data;
                    // when data is ready re-update the total of the cart
                    $rootScope.updateTotal();
                });
            };

            // load the prices
            $rootScope.tickets = [];
            $rootScope.loadPrices();


            /* init of plays (retriving plays from server) */
            $rootScope.loadPlaysByDate = function () {
                CompletePlays.playsByDate().then(
                    function (data) {
                        $rootScope.playsByDate = data;
                    },
                    function (error) {
                        $rootScope.playsByDate = [];
                        $location.path("/error");
                    }
                );
            };

            $rootScope.loadPlaysByDate();


            /* init of cart */

            // load the cart from LocalStorage
            var loadCart = function () {
                $rootScope.cart = StorageService.loadCart();
                if ($rootScope.cart == null) {
                    $rootScope.cart = [];
                }

                // ask to server if the cart loaded is valid using 'buyProcedure validation'
                BuyProcedure.proceed($rootScope.cart)
                    .success(function () {  // tutto ok
                    })
                    .error(function (data, status) {    // biglietti o spettacoli non più disponibili
                        if (status == 409) {
                            // ricarico il carrello fornitomi dal server
                            $rootScope.cart = data;
                        } else {
                            // annullo il carrello
                            $rootScope.cart = [];
                        }
                    });
            };

            // carrello che contiene oggetti film modificati
            $rootScope.cart = $rootScope.cart || [];
            $rootScope.total = $rootScope.total || 0.00;

            loadCart();

            // on cart changes, save it in LocalStorage and update the total
            $rootScope.$watch(function () {
                return $rootScope.cart;
            }, function (cart) {
                $rootScope.updateTotal();
                StorageService.saveCart(cart);
            }, true);


            $rootScope.cartLength = function () {
                return $rootScope.cart.length;
            };


            /* init buy variables */

            // oggetto per la gestione dei dati di acquisto da scambiare con il server
            $rootScope.buy = {
                shared_obj: {}, // mappa sala e posti selezionati da renderizzare
                data_from_server: [],
                data_from_server_index: -1,
                data_to_server: {},
                complete_error: true // errore alla fine della procedura di acquisto
            };

            $rootScope.buy.data_to_server.cart = [];
        }]);

    /* function used in $routeProvider to route a user to login page if is not logged */
    function checkRouting ($rootScope, $location, Auth) {
        if ($rootScope.isUserLoggedPromise != undefined) {
            // check the promise
            $rootScope.isUserLoggedPromise.then(
                function (data) {
                    // user is logged. continue
                    // check if normal user is trying to access to an admin page
                    if (data != undefined) {
                        if (data.role != "admin" && $location.path() != "/me") {
                            $location.path("/error");
                        }
                    }
                }, function () {
                    // user isn't logged. Redirect to login page
                    $location.path("/login");
                });
        } else {
            // ask to server if user is logged
            Auth.me().then(function () {
                // user is already logged
                $rootScope.isUserLogged = true;
                //save basic user data
                $rootScope.user = data;
                // countinue the routing
                // check if normal user is trying to access to an admin page
                if (data.role != "admin" && $location.path() != "/me") {
                    $location.path("/error");
                }
            }, function () {
                $rootScope.isUserLogged = false;
                $rootScope.user = {};
                // user isn't logged. Redirect to login page
                $location.path("/login");
            });
        }
    }

})();
