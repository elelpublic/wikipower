angular.module('PlayApp', [])
  .controller('PlayController', ['$scope', function($scope) {
    $scope.naomi = { name: 'Naomi' };
    $scope.igor = { name: 'Igor' };
    $scope.refresh = function() {
      console.log( 'Refreshing ' + $scope.name );
    };
  }])
  .directive('myCustomer', function() {
    return {
      restrict: 'E',
      transclude: true,
      scope: {
        person: '=',
        //onRefresh: '&onRefresh'
        onRefresh: function() {
          console.log( $scope.person );
        }
      },
      templateUrl: 'templates/my-customer.html'
      //template: 'Name: {{customer.name}} Address: {{customer.address}}'
    };
  });