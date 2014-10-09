describe('DatabasesDir', function () {
	var path = '/Users/floriantopf/Documents/CAMPUS02/MA-Courses/DAB/impex-portal/public/';
	
	beforeEach(angular.mock.module('portal'));
	
	describe('template', function(){
		var $compile, scope, cService, $httpBackend;
		
		beforeEach(module('templates'));
		
		beforeEach(inject(function(_$compile_, $rootScope, configService, _$httpBackend_){
			$compile = _$compile_;
			scope = $rootScope.$new();
			cService = configService;
			$httpBackend = _$httpBackend_;
			
		    jasmine.getJSONFixtures().fixturesPath=path+'js/test/mock';
		    cfg = getJSONFixture('config.json')
			cService.config = cfg;
		}));
		
		it('should render the template with config info', inject(function(){
			var template = $compile('<databases-dir></databases-dir>')(scope);
			scope.$digest();
			var templateAsHtml = template.html();
			
			expect(scope.dbdirvm.config).toEqual(cfg);
			for(var i in cfg.databases){
				expect(templateAsHtml).toContain(cfg.databases[i].id);
				expect(templateAsHtml).toContain(cfg.databases[i].type);
				expect(templateAsHtml).toContain(cfg.databases[i].name);
			}
		}));
		
		
	});
 
     
});