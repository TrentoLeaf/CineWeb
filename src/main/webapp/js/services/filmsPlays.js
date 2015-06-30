(function () {
    'use strict';

    Array.prototype.toMap = function (key) {
        var map = {};
        this.forEach(function (obj) {
            map[obj[key]] = obj;
        });
        return map;
    };

    Array.prototype.toArrayMap = function (key) {
        var map = {};
        this.forEach(function (obj) {
            map[obj[key]] = map[obj[key]] || [];
            map[obj[key]].push(obj);
        });
        return map;
    };

    angular.module('filmsPlaysModule', ['constantsModule', 'ngResource'])

        .factory('Films', ['BASE', '$resource', function (BASE, $resource) {
            return $resource(BASE + '/films/:id', {id: '@id'}, {
                update: {
                    method: 'PUT'
                }
            });
        }])

        .factory('Plays', ['BASE', '$resource', function (BASE, $resource) {
            return $resource(BASE + '/plays/:id', {id: '@id'}, {
                update: {
                    method: 'PUT'
                }
            });
        }])

        .factory('CompletePlays', ['Films', 'Plays', '$q', '$log', '$filter', function (Films, Plays, $q, $log, $filter) {
            return {

                playsByDate: function () {
                    var deferred = $q.defer();

                    $q.all([Films.query().$promise, Plays.query().$promise]).then(
                        function (results) {

                            // map films: fid -> film
                            var filmsObj = results[0].toMap("fid");

                            // add films and date to play
                            var withDate = results[1].map(function (obj) {
                                obj.date = obj.time.split("T")[0];
                                //obj.film = filmsObj[obj.fid];
                                return obj;
                            });

                            var array = [];

                            var perDate = withDate.toArrayMap("date");
                            for (var key in perDate) {
                                if (perDate.hasOwnProperty(key)) {
                                    array.push({date: key, films: perDate[key]});
                                }
                            }

                            array.forEach(function (current) {
                                var map = current.films.toArrayMap('fid');
                                current.films = [];
                                for (var key in map) {
                                    if (map.hasOwnProperty(key)) {
                                        var o = angular.copy(filmsObj[key]);
                                        o.plays = map[key];
                                        o.plays.map(function (t) {
                                            delete t.fid;
                                            o.date = t.date;
                                            t.time = $filter('date')(t.time, 'HH:mm');
                                            return t;
                                        });
                                        current.films.push(o);
                                    }
                                }
                            });

                            deferred.resolve(array);
                        },
                        function (errors) {
                            deferred.reject(errors);
                        }
                    );

                    return deferred.promise;
                }

            }
        }]);

})();