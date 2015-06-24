(function () {
    'use strict';

    angular.module('adminFilms', ['filmsPlaysModule'])
        .controller('AdminFilmsController', ['$location', 'Films', function ($location, Films) {


            var ctrl = this;
            this.newFilm = new Films();


            var init = function () {

                ctrl.loading = true;
                ctrl.films = [];
            };

            this.loadFilms = function () {

                init();

                Films.query(function (data) {
                    ctrl.films = data;
                    ctrl.loading = false;
                });
            };

            this.addFilm = function () {

                ctrl.newFilm.$save( function (data) {
                    ctrl.films.push(data);
                    ctrl.newFilm = new Films();

                });
            }

            this.loadFilms();

        }]);


})();
