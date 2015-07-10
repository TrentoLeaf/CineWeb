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

    /* modulo per la comunicazione con il server dei dati relativi alle proiezioni e ai films */
    angular.module('filmsPlaysModule', ['constantsModule', 'ngResource'])

        /* metodi http per la gestione dei film */
        .factory('Films', ['BASE', '$resource', function (BASE, $resource) {
            return $resource(BASE + '/films/:id', {id: '@id'}, {
                update: {
                    method: 'PUT'
                }
            });
        }])

        /* metodi http per la gestione delle proiezioni */
        .factory('Plays', ['BASE', '$resource', function (BASE, $resource) {
            return  $resource(BASE + '/plays/:id', {id: '@id'}, {
                update: {
                    method: 'PUT'
                }
            });
        }])

        .factory('CompletePlays', ['Films', 'Plays', '$q', '$log', '$filter', function (Films, Plays, $q, $log, $filter) {
            return {

                /* recupera la lista delle proiezioni disponibili sul server
                 * raggruppandole per data
                 */
                playsByDate: function () {
                    var deferred = $q.defer();

                    // richiesta sincrona di film e proiezioni
                    $q.all([Films.query().$promise, Plays.query().$promise]).then(

                        // raggruppamento dati proiezioni e film
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

                            // group by date
                            var perDate = withDate.toArrayMap("date");
                            for (var key in perDate) {
                                if (perDate.hasOwnProperty(key)) {
                                    array.push({date: key, films: perDate[key]});
                                }
                            }

                            // populate the array
                            array.forEach(function (current) {
                                var map = current.films.toArrayMap('fid');
                                current.films = [];
                                for (var key in map) {
                                    if (map.hasOwnProperty(key)) {
                                        var o = angular.copy(filmsObj[key]);
                                        o.plays = map[key];
                                        o.plays.map(function (t) {
                                            delete t.fid;
                                            delete t.date;
                                            return t;
                                        });
                                        o.date = current.date;
                                        current.films.push(o);
                                    }
                                }
                            });

                            // comparison by date function
                            function compare(a,b) {
                                var dateA = new Date(a.date);
                                var dateB = new Date(b.date);

                                if (dateA < dateB)
                                    return -1;
                                if (dateA > dateB)
                                    return 1;
                                return 0;
                            }

                            // order array by date
                            array.sort(compare);

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