describe('DatabasesDir', function () {
	var path = '/Users/floriantopf/Documents/CAMPUS02/MA-Courses/DAB/impex-portal/public/';
	
	beforeEach(angular.mock.module('portal'));
	
	describe('template', function(){
		var $compile, scope, cfg, statusMap, cService, template, $httpBackend;
		
		beforeEach(module('templates'));
		
		beforeEach(inject(function(_$compile_, $rootScope, configService, _$httpBackend_){
			$compile = _$compile_;
			scope = $rootScope.$new();
			cService = configService;
			$httpBackend = _$httpBackend_;
			
		    jasmine.getJSONFixtures().fixturesPath=path+'js/test/mock';
		    cfg = getJSONFixture('config.json');
		    statusMap = getJSONFixture('statusMap.json');
			cService.config = cfg;
		    
		    $httpBackend.when('GET', '/config?fmt=json').respond(cfg);
		    $httpBackend.when('GET', '/registry/status').respond(statusMap);
		    
		    // compiled template
		    template = createDirective();
		}));
		
		function createDirective(){
			var template = $compile(angular.element('<databases-dir></databases-dir>'))(scope);
			scope.$digest();
			return template;
		}
		
		it('should render the template with config/status info', inject(function(){
			// a little hack (promise doesn't get resolved?)
			scope.dbdirvm.statusMap = statusMap;
			scope.$digest();
			var templateAsHtml = template.html();
			expect(scope.dbdirvm.config).toEqual(cfg);
			for(var i in cfg.databases){
				if(cfg.databases[i].portal) {
					expect(templateAsHtml).toContain(cfg.databases[i].tree);
					expect(templateAsHtml).toContain(cfg.databases[i].methods);
					expect(templateAsHtml).toContain(cfg.databases[i].type);
					expect(templateAsHtml).toContain(cfg.databases[i].name);
					expect(templateAsHtml).toContain(statusMap[cfg.databases[i].id].lastUpdate);
				}
			}
		}));
		
		
	});
 
     
});