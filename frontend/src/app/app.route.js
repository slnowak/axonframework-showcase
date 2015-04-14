'use strict';

var appRouting = angular.module('appRouting', ['ui.router']);

appRouting.config(function ($stateProvider, $urlRouterProvider) {

    $urlRouterProvider.otherwise('/');

    $stateProvider
        .state('root', {
            url: '',
            abstract: true,
            views: {
                'menu-navbar': {
                    templateUrl: 'app/shared/menu-bar/menu-bar.html'
                },
                'jumbotron': {
                    templateUrl: 'app/shared/jumbotron/jumbotron.html'
                }
            }
        })
        .state('root.home', {
            url: '/',
            views: {
                'content@': {
                    templateUrl: 'app/components/kanbanboard/kanbanBoardView.html',
                    controller: 'boardController'
                }
            }
        })
        .state('root.announcement', {
            url: 'announcement',
            views: {
                'content@': {
                    templateUrl: 'app/components/announcement/publishing/publishForm.html',
                    controller: 'announcementPublishingController'
                }
            }
        })
        .state('root.announcement.config', {
            url: '/announcement/config',
            views: {
                'content@': {
                    templateUrl: 'app/components/announcement/config/configTab.html',
                    controller: 'announcementConfigController'
                }
            }
        })
        .state('root.announcement.config.facebook', {
            url: '/facebook',
            templateUrl: 'app/components/announcement/config/facebook/facebook.html',
            controller: 'facebookPropertiesController'
        })
        .state('root.announcement.config.twitter', {
            url: '/twitter',
            templateUrl: 'app/components/announcement/config/twitter/twitter.html',
            controller: 'twitterPropertiesController'
        })
        .state('root.announcement.config.googlegroup', {
            url: '/googlegroup',
            templateUrl: 'app/components/announcement/config/googlegroup/googlegroup.html',
            controller: 'googlegroupPropertiesController'
        })
        .state('root.announcement.config.board', {
            url: '/board',
            templateUrl: 'app/components/announcement/config/board/board.html',
            controller: 'boardPropertiesController'
        })

});