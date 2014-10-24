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
        spyOn(window, 'confirm').andReturn(true);
		
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
	        spyOn(growlService, 'info');
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
			expect(scope.userdirvm.user.selections.length).toBe(3);
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
		
		it('should toogle selection details on user action', function(){
			// faking set-applyable-elements
			scope.userdirvm.applyableElements = ['NumericalOutput'];
			var selId = '1c75698b';
			// was uncollapsed on init
			scope.userdirvm.toggleSelectionDetails(selId);
			expect(scope.userdirvm.isCollapsed[selId]).toBeTruthy();
			expect(scope.userdirvm.user.activeSelection).toEqual([]);
			expect(scope.userdirvm.isSelApplyable).toBeFalsy();
			// was collapsed
			scope.userdirvm.toggleSelectionDetails(selId);
			expect(scope.userdirvm.isCollapsed[selId]).toBeFalsy();
			for(var id in scope.userdirvm.isCollapsed){
				if(id != selId) expect(scope.userdirvm.isCollapsed[id]).toBeTruthy();
			}
			expect(scope.userdirvm.isSelApplyable).toBeTruthy();
			expect(scope.userdirvm.user.activeSelection).toEqual([user.selections[1]]);
		});
		
		it('should toogle result/votable details on user action', function(){
			var votId = '54402e0a30041b4daf106ee1';
			// was collapsed on init
			scope.userdirvm.toggleDetails(votId);
			expect(scope.userdirvm.user.activeSelection).toEqual([]);
			expect(scope.userdirvm.isCollapsed[votId]).toBeFalsy();
			for (var id in scope.userdirvm.isCollapsed){
				if(id != votId) expect(scope.userdirvm.isCollapsed[id]).toBeTruthy();
			}
			// @TODO check if the isRead routine works
		});
		
		it('should delete selection on user action', function(){
			var selId = 'defde9a2';
			scope.userdirvm.deleteSelection(selId);
			expect(scope.userdirvm.user.selections.length).toBe(1);
			expect(scope.userdirvm.user.activeSelection).toEqual([]);
			expect(scope.userdirvm.isCollapsed[selId]).toBeUndefined();
			expect(scope.userdirvm.growl.info).toHaveBeenCalled();
		});
		
		it('should delete votable on user action', function(){
			var vot = user.voTables[0];
			scope.userdirvm.deleteVOTable(vot);
			expect(scope.userdirvm.user.voTables).toEqual([]);
			expect(scope.userdirvm.isCollapsed[vot.id]).toBeUndefined();
			expect(scope.userdirvm.growl.info).toHaveBeenCalled();
		});
		
		it('should delete result on user action', function(){
			var resId = '240bf200';
			scope.userdirvm.deleteResult(resId);
			expect(scope.userdirvm.user.results.length).toBe(1);
			expect(scope.userdirvm.isCollapsed[resId]).toBeUndefined();
			expect(scope.userdirvm.growl.info).toHaveBeenCalled();
		});
		
		it('should apply selection on user action', function(){
			var resourceId = user.selections[1].elem.resourceId;
			scope.userdirvm.applySelection(resourceId);
			expect(scope.$broadcast).toHaveBeenCalledWith(
					'apply-selection', resourceId, 
					['Density', 'Ux,Uy,Uz', 'Utot', 'Pressure', 'Temperature'])
		});
		
		it('should apply votable on user action', function(){
			var url = user.results[0].content.message;
			scope.userdirvm.applyVOTable(url);
			expect(scope.$broadcast).toHaveBeenCalledWith('apply-votable', url);
		});
		
		// @TODO maybe check more here
		it('should clear different data on user action', function(){
			scope.userdirvm.clearData('selections');
			// only one must be deleted (see current repository == FMI)
			expect(scope.userdirvm.user.selections.length).toEqual(1);
			expect(scope.userdirvm.user.activeSelection).toEqual([]);
			scope.userdirvm.clearData('votables');
			expect(scope.userdirvm.user.voTables).toEqual([]);
			scope.userdirvm.clearData('results');
			expect(scope.userdirvm.user.results.length).toEqual(1);
			expect(scope.userdirvm.growl.info).toHaveBeenCalled();
		});
		
		/*it('should send data to samp on user action', function(){
			
			
		});*/
		
		
	});
	
	// @TODO add methods template with filter
	

});