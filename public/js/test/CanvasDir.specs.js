describe('CanvasDir', function () {
	var path = '/Users/floriantopf/Documents/CAMPUS02/MA-Courses/DAB/impex-portal/public/';
	
	beforeEach(angular.mock.module('portal'));
	
	describe('template', function(){
		var $compile, scope, timeout, cService, $httpBackend, element, canvas, ctx, cfg;
		
		beforeEach(module('templates'));
		
		beforeEach(inject(function(_$compile_, $rootScope, $timeout, configService, _$httpBackend_){
			$compile = _$compile_;
			scope = $rootScope.$new();
			timeout = $timeout;
			cService = configService;
			$httpBackend = _$httpBackend_;
			
			// mocking the canvas methods
			ctx = {
				lineWidth: jasmine.createSpy('lineWidth'),
				clearRect: jasmine.createSpy('clearRect'),
				beginPath: jasmine.createSpy('beginPath'),
				moveTo: jasmine.createSpy('moveTo'),
				lineTo: jasmine.createSpy('lineTo'),
				stroke: jasmine.createSpy('stroke')
			}
			canvas = {
				getContext: jasmine.createSpy('getContext').andReturn(ctx),
				height: null,
				width: null
			}
			
			// spying on broadcast events
	        spyOn(scope, '$broadcast').andCallThrough();
	        spyOn(scope, '$on').andCallThrough();
	        spyOn(scope, '$watch').andCallThrough();
	        
		    // spying on jquery selectors (and returning dummy values)
			spyOn($.fn, 'offset').andReturn({ left: 2, top: 2 });
			spyOn($.fn, 'outerHeight').andReturn(2);
			spyOn($.fn, 'outerWidth').andReturn(2);
			spyOn($.fn, 'width').andReturn(4);
			spyOn($.fn, 'height').andReturn(4);
			//spyOn($.fn, 'hide');
			//spyOn($.fn, 'show');
			//spyOn($.fn, 'one').andCallThrough();
	        
			// spying on getElementById
			document.getElementById = jasmine.createSpy('getElementById').andReturn(canvas);
			element = angular.element('<canvas-dir id="canvas-dir"></canvas-dir>');
			
		    jasmine.getJSONFixtures().fixturesPath=path+'js/test/mock';
		    cfg = getJSONFixture('config.json');
			cService.config = cfg;
		    scope.configService = cService;
		    
		    // timeout.flush() needs empty responses here
			$httpBackend.when('GET', '/config?fmt=json').respond('');
			$httpBackend.when('GET', '/userdata').respond('');
			$httpBackend.when('GET', '/filter/region').respond('');
		    
		}));
		
		function createDirective(){
			var template = $compile(element)(scope);
			scope.$digest();
			spyOn(scope.canvasdirvm, 'clear');
			return template;
		}
		
		it('should render the template', function(){
			var template = createDirective();
			var templateAsHtml = template.html();
			expect(templateAsHtml).toEqual('<canvas id="canvas"></canvas>');
			expect(scope.configService.config).toEqual(cfg);
		});
		
		it('should handle resize', function(){
			var template = createDirective();
			timeout.flush();
			expect(scope.$watch).toHaveBeenCalled();
			//scope.$digest();
			expect(scope.canvasdirvm.main).toEqual({ left: 2, top: 2 });
			expect(template.offset().top).toEqual(2);
			expect(template.offset().left).toEqual(2);
			expect(scope.canvasdirvm.height).toEqual(4);
			expect(scope.canvasdirvm.width).toEqual(4);
			expect(canvas.height).toEqual(4);
			expect(canvas.width).toEqual(4);
		});
		
		it('should react on database-success broadcast', function(){
			var template = createDirective();
		    scope.$broadcast('database-success', 'spase://IMPEX/Repository/FMI/HYB');
		    expect(scope.$on).toHaveBeenCalled();
		    expect(scope.canvasdirvm.clear).toHaveBeenCalled();
	        expect(scope.canvasdirvm.activeDatabase).toEqual('FMI-HYBRID');		
	        expect(scope.canvasdirvm.activeService).toBeNull();	
	        expect(scope.canvasdirvm.database).toEqual({ left: 2, top: 2 });
	        expect(scope.canvasdirvm.service).toEqual({ left: 2, top: 2 });
	        expect(scope.canvasdirvm.myData).toEqual({ left: 2, top: 2 });
	        expect(scope.canvasdirvm.elemH).toEqual(2)
	        expect(scope.canvasdirvm.elemW).toEqual(2)

		});
		
		it('should react on service-success broadcast', function(){
			var template = createDirective();
		    scope.$broadcast('service-success', 'spase://IMPEX/Repository/FMI/HYB');
		    expect(scope.$on).toHaveBeenCalled();
		    expect(scope.canvasdirvm.clear).toHaveBeenCalled();
	        expect(scope.canvasdirvm.activeService).toEqual('FMI-HYBRID');	
	        expect(scope.canvasdirvm.activeDatabase).toBeNull();	
	        expect(scope.canvasdirvm.service).toEqual({ left: 2, top: 2 });
	        expect(scope.canvasdirvm.myData).toEqual({ left: 2, top: 2 });
	        expect(scope.canvasdirvm.tools).toEqual({ left: 2, top: 2 });
	        expect(scope.canvasdirvm.elemH).toEqual(2)
	        expect(scope.canvasdirvm.elemW).toEqual(2)
		});
		
		it('should clear paths on request', function(){
			var template = createDirective();
			scope.$broadcast('clear-paths');
	        expect(scope.canvasdirvm.activeService).toBeNull();	
	        expect(scope.canvasdirvm.activeDatabase).toBeNull();	
	        expect(scope.canvasdirvm.clear).toHaveBeenCalled();
		});
		
		
	});
 
     
});