(function () {
    "use strict";


    angular.module('PlaysModule', ['filmsPlaysModule'])
        .controller('PlaysController', ['$location', 'CompletePlays', function ($location, CompletePlays) {
            this.current = {};

            var ctrl = this;

            this.cart = [];
            this.archive = [];

            this.loadData = function () {
                CompletePlays.playsByDate().then(
                    function (data) {
                        ctrl.archive = data;
                    },
                    function (error) {
                        // TODO: handle error
                    }
                );
            };

            this.isNow = function(date){
                var act = new Date();
                console.log("This is the date: "+date+" and this is the actual: "+act);
                return true;
            };

            this.setCurrent = function (date, film) {
                this.current = this.archive[date].films[film];
                this.current['date'] = date;
            };

        this.addToCart = function(film) {
                film['normale'] = 1;
                film['ridotto'] = 0;
                film['militari'] = 0;
                film['disabili'] = 0;
                this.cart.push(film);
            };

        this.removeFromCart = function(film) {

            var i = this.cart.indexOf(film);
            if (i > -1) {
                this.cart.splice(film);
            }
        };

        this.updateTiket = function (film, type, number) {
            film[type] = number;
        }

        this.updateTotal = function () {


        };

    });
})();