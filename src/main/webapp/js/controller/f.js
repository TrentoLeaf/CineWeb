(function () {
    'use strict';

    angular.module('test', ['filmsPlaysModule', 'constantsModule'])
        .controller('TestController', ['$location', 'Films', 'Plays', 'CompletePlays', function ($location, Films, Plays, CompletePlays) {

            var ctrl = this;

            this.data = [];
            this.error = "";

            var setData = function (data) {
                ctrl.data = data;
                ctrl.error = "";
            };

            var setError = function (error) {
                ctrl.data = [];
                ctrl.error = error;
            };

            this.films = function () {
                Films.query().$promise.then(
                    function (data) {
                        setData(data);
                    },
                    function (error) {
                        setError(error);
                    }
                );
            };

            this.plays = function () {
                Plays.query().$promise.then(
                    function (data) {
                        setData(data);
                    },
                    function (error) {
                        setError(error);
                    }
                );
            };

            this.playsByDate = function () {
                CompletePlays.playsByDate().then(
                    function (data) {
                        setData(data);
                    },
                    function (error) {
                        setError(error);
                    }
                );
            };

        }]);

})();
