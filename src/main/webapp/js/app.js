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

});

/* può tornare utile
 function close_Sidediv() {
 $('.side-div').removeClass('side-div-w');
 $('.side-div').find('.side-nav-element').addClass('ng-hide');
 }*/

(function () {
    'use strict';

    angular.module('cineweb', ['ngRoute', 'uiGmapgoogle-maps', 'cartModule', 'PlaysModule', 'tabmodule', 'loginModule',
        'registrationModule', 'meModule', 'adminUsers', 'adminFilms', 'adminPrices', 'adminPlays', 'confirmModule', 'buyModule', 'pricesModule', 'mapModule', 'adminStats'])

        .config(['$routeProvider', function ($routeProvider) {
            $routeProvider.when('/', {
                redirectTo: '/today'
            }).when('/home', {
                redirectTo: '/today'
            }).when('/today', {
                templateUrl: '../partials/today.html',
                controller: 'PlaysController',
                controllerAs: 'ctrl'
            }).when('/soon', {
                templateUrl: '../partials/soon.html',
                controller: 'PlaysController',
                controllerAs: 'ctrl'
            }).when('/info', {
                templateUrl: '../partials/info.html'
                /* TODO remove
                 controller: function (prices) {
                 this.prices = prices;
                 },
                 controllerAs: 'pricesCtrl',
                 resolve: {
                 prices: ['Prices', function (Prices) {
                 return Prices.query().$promise;
                 }]
                 }
                 */
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
                controllerAs: 'ctrl'
            }).when('/login', {
                templateUrl: '../partials/login.html',
                controller: 'LoginController',
                controllerAs: 'c'
            }).when('/confirm', {
                templateUrl: '../partials/confirm.html',
                controller: 'ConfirmController',
                controllerAs: 'ctrl'
            }).when ('/admin',{
                templateUrl: '../partials/admin/dashboard.html'
            }).when ('/admin/films',{
                templateUrl: '../partials/admin/films.html',
                controller: 'AdminFilmsController',
                controllerAs: 'ctrl'
            }).when ('/admin/films/new',{
                templateUrl: '../partials/admin/new_film.html',
                controller: 'AdminFilmsController',
                controllerAs: 'ctrl'
            }).when ('/admin/films/:fid',{
                templateUrl: '../partials/admin/edit_film.html',
                controller: 'AdminFilmsEditController',
                controllerAs: 'ctrl'
            }).when ('/admin/plays',{
                templateUrl: '../partials/admin/plays.html',
                controller: 'AdminPlaysController',
                controllerAs: 'ctrl'
            }).when ('/admin/plays/new',{
                templateUrl: '../partials/admin/new_play.html',
                controller: 'AdminPlaysController',
                controllerAs: 'ctrl'
            }).when ('/admin/plays/:pid', {
                templateUrl: '../partials/admin/edit_play.html',
                controller: 'AdminPlaysEditController',
                controllerAs: 'ctrl'
            }).when ('/admin/prices',{
                templateUrl: '../partials/admin/prices.html',
                controller: 'AdminPricesController',
                controllerAs: 'ctrl'
            }).when ('/admin/rooms',{
                templateUrl: '../partials/admin/rooms.html'
            }).when ('/admin/stats',{
                templateUrl: '../partials/admin/stats.html',
                controller: 'AdminStatsController',
                controllerAs: 'ctrl'
            }).when('/admin/users', {
                templateUrl: '../partials/admin/users.html',
                controller: 'AdminUsersController',
                controllerAs: 'ctrl'
            }).when('/admin/users/new', {
                templateUrl: '../partials/admin/new_user.html',
                controller: 'AdminUsersController',
                controllerAs: 'ctrl'
            }).when('/admin/users/:uid', {
                templateUrl: '../partials/admin/edit_user.html',
                controller: 'AdminUsersEditController',
                controllerAs: 'ctrl'
            }).when('/buy', {
                templateUrl: '../partials/buy_seats.html',
                controller: 'BuyController',
                controllerAs: 'ctrl'
            }).when('/buy_last_step', {
                templateUrl: '../partials/buy_summary.html',
                controller: 'BuyController',
                controllerAs: 'ctrl'
            }).when('/test', {
                templateUrl: '../partials/test.html',
                controller: 'BuyController',
                controllerAs: 'ctrl'
            }).when('/error', {
                templateUrl: '../partials/error.html'
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

        .directive('loading', function () {
            return {
                restrict: 'E',
                templateUrl: '../partials/directives/loading.html',
                scope: {
                    loading: '=loading'
                }
            }
        })

        .run(['$rootScope', '$location', 'Prices', 'StorageService', 'Auth', function ($rootScope, $location, Prices, StorageService, Auth) {

            // redirect only if needed
            var redirect = function (path) {
                if ($location.path() != path) {
                    $location.path(path);
                }
            };

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
                                console.log(num);
                            }
                        }
                        $rootScope.total = $rootScope.total + ($rootScope.tickets[j]['price'] * num);
                    }
                }
                console.log("NEW TOTAL: " + $rootScope.total);
            };


            /* init of prices */
            console.log("INIT THE PRICES");

            // function to load the prices
            var loadPrices = function () {
                Prices.query(function (data) {
                   /* data.map(function (o) {
                        o.type = o.type.capitalizeFirstLetter();
                    });*/
                    $rootScope.tickets = data;
                    // when data is ready re-update the total of the cart
                    $rootScope.updateTotal();
                });
            };

            // load the prices
            $rootScope.tickets = [];
            loadPrices();



            /* init of cart */

            // load the cart from LocalStorage
            var loadCart = function () {
                $rootScope.cart = StorageService.loadCart();
                if ($rootScope.cart != null) {
                    console.log("unhide");
                    // abilita il pulsante 'prosegui'
                    $('#btn-go-to-buy').removeClass('hide');
                } else {
                    $rootScope.cart = [];
                }
                console.log("cart loaded: " + $rootScope.cart);
            };

            // carrello che contiene oggetti film modificati
            $rootScope.cart = $rootScope.cart || [];
            $rootScope.total = $rootScope.total || 0.00;

            loadCart();

            // when cart is changed, save it and update the total
            $rootScope.$watch(function() {return $rootScope.cart;}, function(cart) {

                $rootScope.updateTotal();
                StorageService.saveCart(cart);
                console.log("cart saved");
            }, true);

            /* init of login data */
            $rootScope.user = {};
            $rootScope.isUserLogged = false;
            $rootScope.loginError = "";
            $rootScope.afterLogin = "normal"; // variavile per sapere dove redirigere dopo un login (normal, buy, userArea)

            // request to server the data of a logged user. If the user isn't logged set the login variables.
            var retriveLoginData = function () {
                Auth.me()
                    .success(function (user) {
                        console.log("THE USER IS ALREADY LOGGED");
                        console.log(user);

                        $rootScope.isUserLogged = true;
                        //save basic user data
                        $rootScope.user = user;
                    }).error(function (error) {
                        console.log("THE USER IS NOT LOGGED");

                        $rootScope.isUserLogged = false;
                        $rootScope.user = {};
                    });
            };

            retriveLoginData();



        }]);

})();
