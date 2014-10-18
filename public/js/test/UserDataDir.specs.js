'use strict'

describe('UserDataDir', function() {
	var path = '/Users/floriantopf/Documents/CAMPUS02/MA-Courses/DAB/impex-portal/public/'
		
	var $compile, scope, rService, mService, uService, sService, user, window, state, growlService, 
		element, template, $httpBackend;
	
	beforeEach(module('templates'));
	
	beforeEach(angular.mock.module('portal'));
		
	beforeEach(angular.mock.inject(function(_$compile_, $rootScope, registryService, 
			methodsService, userService, sampService, $window, $state, growl, _$httpBackend_) {
		
		$compile = _$compile_;
		scope = $rootScope.$new();
		rService = registryService;
		mService = methodsService;
		uService = userService;
		sService = sampService;
		window = $window;
		state = $state;
		growlService = growl
		$httpBackend = _$httpBackend_;
		
		// spying on broadcast events
        spyOn(scope, '$broadcast').andCallThrough();
        spyOn(scope, '$on').andCallThrough();
		
        jasmine.getJSONFixtures().fixturesPath=path+'js/test/mock';
        // just creating a mock user
        user = getJSONFixture('user.json');
        uService.user = user;
        // setting a mock state
        state.current.name = 'app.portal.userdata';
		
		// test element 
		element = angular.element('<user-data-dir></user-data-dir>');
		// compiled template
		template = createDirective();
	}));
		
	function createDirective(){
		var template = $compile(element)(scope);
		// setting the db attribute
		//scope.db = null
		scope.$digest();
		return template;
	}	
	
	// everything in $includeContentLoaded is done too!
	// standard case without db filter
	it('should render the template and init variables', function(){
		var templateAsHtml = template.html();
		expect(templateAsHtml).toContain('nav-tabs');
		expect(scope.userdirvm.repositoryId).toBeNull();
		expect(scope.userdirvm.isCollapsed).toEqual({ 
			'defde9a2': true, 
			'54402e0a30041b4daf106ee1': true, 
			'9dda50fa': true });
		expect(scope.userdirvm.isResRead).toEqual({ '9dda50fa': true });
		expect(scope.userdirvm.tabsActive).toEqual([true, false, false]);
		expect(scope.userdirvm.selectables.length).toBe(0);
		expect(scope.userdirvm.applyableElements.length).toBe(0);
		expect(scope.userdirvm.applyableModel).toBeNull();
		expect(scope.userdirvm.isSelApplyable).toBeFalsy();
		expect(scope.userdirvm.isVOTApplyable).toBeFalsy();
		expect(scope.userdirvm.isSampAble).toBeFalsy();
		expect(scope.userdirvm.isLogCollapsed).toBeTruthy();
		expect(scope.userdirvm.user).toEqual(user);
		expect(scope.userdirvm.registryService).toEqual(rService);
		expect(scope.userdirvm.methodsService).toEqual(mService);
		expect(scope.userdirvm.userService).toEqual(uService);
		expect(scope.userdirvm.sampService).toEqual(sService);
		expect(scope.userdirvm.window).toEqual(window);
		expect(scope.userdirvm.state).toEqual(state);
	});	
	
	
});