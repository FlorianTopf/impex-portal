'use strict'

describe('UserDataDir', function() {
	var path = '/Users/floriantopf/Documents/CAMPUS02/MA-Courses/DAB/impex-portal/public/'
		
	var $compile, scope, rService, mService, uService, sService, user, window, state, growlService, 
	element, template, simDb, selection, $httpBackend;
	
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
        // needed for non-default states
		simDb = getJSONFixture('simDatabase.json');
		selection = getJSONFixture('selection.json')
		
	}));
	
	// standard case without db filter 
	describe('default template', function() {		
		beforeEach(function(){
			// default element 
			element = angular.element('<user-data-dir></user-data-dir>');
	        // setting the mock state
	        state.current.name = 'app.portal.userdata';
			// compiled template
	        template = createDefaultDirective();
	        
		});
		
		function createDefaultDirective(){
			var template = $compile(element)(scope);
			scope.$digest();
			return template;
		}
		
		// everything in $includeContentLoaded is done too!
		it('should be rendered and init variables', function(){
			var templateAsHtml = template.html();
			expect(templateAsHtml).toContain('nav-tabs');
			expect(scope.userdirvm.repositoryId).toBeUndefined();
			// there no active (uncollapsed) selection in default
			expect(scope.userdirvm.isCollapsed).toEqual({ 
				'defde9a2': true, 
				'54402e0a30041b4daf106ee1': true, 
				'9dda50fa': true,
				'240bf200' : true,
				// active selection is uncollapsed
				'1c75698b': false });
			expect(scope.userdirvm.isResRead).toEqual({ '9dda50fa': true, '240bf200' : true });
			expect(scope.userdirvm.tabsActive).toEqual([true, false, false]);
			expect(scope.userdirvm.selectables).toEqual([]);
			expect(scope.userdirvm.applyableElements).toEqual([]);
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
		
		it('should correctly handle isSelectable', function(){
			expect(scope.userdirvm.isSelectable('SimulationModel')).toBeFalsy();
		});
		
	});
	
	// registry template with filter
	describe('repository template in registry', function() {
		beforeEach(function(){
			// repository element
			element = angular.element('<user-data-dir db="{{db}}"></user-data-dir>');
	        // setting the mock state
	        state.current.name = 'app.portal.registry';
			// compiled repository template
	        template = createRepositoryDirective();

	        // spying on growl success
	        spyOn(growlService, 'success');
	        spyOn(rService, 'notify');
		});
		
		function createRepositoryDirective(){
			var template = $compile(element)(scope);
			scope.db = simDb.id;
			scope.$digest();
			return template;
		}
			
		it('should be rendered and init variables', function(){
			var templateAsHtml = template.html();
			expect(templateAsHtml).toContain('nav-tabs');
			expect(scope.userdirvm.repositoryId).toEqual(simDb.id);
			// there is a active (uncollapsed) selection
			expect(scope.userdirvm.isCollapsed).toEqual({ 
				'defde9a2': true, 
				'54402e0a30041b4daf106ee1': true, 
				'9dda50fa': true,
				'240bf200' : true,
				// active selection is uncollapsed 
				'1c75698b': false });
			expect(scope.userdirvm.isResRead).toEqual({ '9dda50fa': true, '240bf200' : true });
			expect(scope.userdirvm.tabsActive).toEqual([true, false, false]);
			expect(scope.userdirvm.selectables).toEqual(['NumericalOutput']);
			expect(scope.userdirvm.applyableElements).toEqual([]);
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
		
		it('should react on set-applyable-elements', function(){
			scope.$broadcast('set-applyable-elements', 'NumericalOutput');
			expect(scope.$on).toHaveBeenCalled();
			expect(scope.userdirvm.applyableElements).toEqual(['NumericalOutput'])
			// everything works fine with the mock
			expect(scope.userdirvm.isSelApplyable).toBeTruthy();
		});
		
		it('should react on set-applyable-votable', function(){
			scope.$broadcast('set-applyable-votable', true);
			expect(scope.$on).toHaveBeenCalled();
			expect(scope.userdirvm.isVOTApplyable).toBeTruthy();
		});
		
		// @TODO only works if we use a sinp model as active element
		/* it('should react on set-applyable-model', function(){
			
			
		});*/
		
		it('should react on update-selections', function(){
			var id = user.activeSelection[0].id;
			scope.$broadcast('update-selections', id);
			expect(scope.$on).toHaveBeenCalled();
			expect(scope.userdirvm.isCollapsed[id]).toBeFalsy();
			expect(scope.userdirvm.isCollapsed).toEqual({ 
				'defde9a2': true, 
				'54402e0a30041b4daf106ee1': true, 
				'9dda50fa': true,
				'240bf200' : true,
				// active selection is uncollapsed 
				'1c75698b': false });
			expect(scope.userdirvm.user.activeSelection).toEqual(user.activeSelection);
		});
		
		it('should react on update-votables', function(){
			var id = user.voTables[0].id;
			scope.$broadcast('update-votables', id);
			expect(scope.$on).toHaveBeenCalled();
			expect(scope.userdirvm.isCollapsed[id]).toBeFalsy();
			expect(scope.userdirvm.isCollapsed).toEqual({ 
				'defde9a2': true, 
				// votable is uncollapsed 
				'54402e0a30041b4daf106ee1': false, 
				'9dda50fa': true,
				'240bf200' : true,
				'1c75698b': true});
			expect(scope.userdirvm.user.activeSelection).toEqual([]);
		});
		
		it('should react on update-results', function(){
			var id = user.results[0].id;
			scope.$broadcast('update-results', id);
			expect(scope.$on).toHaveBeenCalled();
			expect(scope.userdirvm.isCollapsed[id]).toBeFalsy();
			expect(scope.userdirvm.isCollapsed).toEqual({ 
				'defde9a2': true, 
				'54402e0a30041b4daf106ee1': true,
				// result is uncollapsed
				'9dda50fa': false,
				'240bf200' : true,
				'1c75698b': true});
			expect(scope.userdirvm.user.activeSelection).toEqual([]);
		});
		
		it('should correctly handle isSelectable', function(){
			expect(scope.userdirvm.isSelectable('SimulationModel')).toBeFalsy();
			expect(scope.userdirvm.isSelectable('NumericalOutput')).toBeTruthy();
		});
		
		it('should correctly handle isSelSaved', function(){
			expect(scope.userdirvm.isSelSaved('1c75698b')).toBeTruthy();
			expect(scope.userdirvm.isSelSaved('boum')).toBeFalsy();
		});
		
		it('should save selection on user action', function(){
			scope.userdirvm.user.activeSelection = [selection];
			scope.userdirvm.saveSelection(selection.id);
			expect(scope.userdirvm.user.selections[0]).toEqual(selection);
			expect(scope.userdirvm.tabsActive).toEqual([true, false, false]);
			expect(scope.userdirvm.isCollapsed).toEqual({ 
				'defde9a2': true, 
				'54402e0a30041b4daf106ee1': true,
				'9dda50fa': true,
				'240bf200' : true,
				'1c75698b': true,
				// saved selection is uncollapsed
				'39026add': false});
			expect(scope.userdirvm.growl.success).toHaveBeenCalled();
			expect(scope.userdirvm.registryService.notify)
				.toHaveBeenCalledWith('success', simDb.id)
			
		});
		
		// @TODO continue with toggleSelectionDetails()
		
	});
	
	// @TODO add methods template with filter
	

});