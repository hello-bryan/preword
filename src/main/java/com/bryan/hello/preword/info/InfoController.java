package com.bryan.hello.preword.info;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.bryan.hello.preword.info.model.City;
import com.bryan.hello.preword.info.model.FileData;
import com.bryan.hello.preword.info.model.Project;
import com.bryan.hello.preword.info.storage.StorageService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("info")
public class InfoController {

	private InfoService infoService;
	private StorageService storageService;
	
	@Autowired
	public InfoController(InfoService infoService, StorageService storageService) {
		this.infoService = infoService;
		this.storageService = storageService;
	}
	
	
	@GetMapping("project")
	public Object projectInfo() {
		log.debug("/info start");
		Project project = infoService.getProjectInfo();
		return project;
	}


	@GetMapping("custom")
	public String customJson() {
		JsonObject jo = new JsonObject();
		
		jo.addProperty("projectName", "preword");
		jo.addProperty("author", "hello-bryan");
		jo.addProperty("createdDate", new Date().toString());
		
		JsonArray ja = new JsonArray();
		for(int i=0; i<5; i++) {
			JsonObject jObj = new JsonObject();
			jObj.addProperty("prop"+i, i);
			ja.add(jObj);
		}
		
		jo.add("follower", ja);
		
		return jo.toString();
	}
	
	@GetMapping("cityList")
	public Object cityList() {
		log.debug("/cityList start");
		List<City> cityList = infoService.getCityList();
		return cityList;
	}
	
	@GetMapping("cityListByCode/{countryCode}/{population}")
	public Object cityByCountryCode(@PathVariable("countryCode") String ctCode, @PathVariable("population") int population) {
		log.debug("countryCode = {}, population = {}", ctCode, population);
		List<City> cityList = infoService.findCityByCodeAndPopulation(ctCode, population);
		return cityList;
	}
	
//	@GetMapping("cityAdd/{name}/{countryCode}/{district}/{population}")
//	public Object cityAdd(@PathVariable(value="name") String name
//			, @PathVariable(value="countryCode") String ctCode
//			, @PathVariable(value="district") String district
//			, @PathVariable(value="population") int population) {
//		
//		log.debug("name = {}, ctCode = {}, district = {}, population ={}", name, ctCode, district, population);
//		
//		return "ok";
//	}
	
//	@GetMapping("cityAdd")
//	public Object cityAdd(@RequestParam(value="name", required=true) String name
//			, @RequestParam(value="countryCode", required=true) String ctCode
//			, @RequestParam(value="district", required=true) String district
//			, @RequestParam(value="population", required = false, defaultValue = "0") int population) {
//		
//		log.debug("name = {}, ctCode = {}, district = {}, population ={}", name, ctCode, district, population);
//		
//		return "ok";
//	}
	
//	@GetMapping(value="cityAdd")
//	public Object cityAdd(City city) {
//		
//		log.debug("city = {}", city.toString());
//		
//		return "ok";
//	}
	
	@PostMapping(value="cityAdd")
	public ResponseEntity<City> cityAdd(@RequestBody City city) {
		try {
			log.debug("city = {}", city.toString());
			return new ResponseEntity<>(infoService.insert(city), HttpStatus.OK);
		}catch (Exception e) {
			log.error(e.toString());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value="cityEdit")
	public ResponseEntity<String> cityEdit(@RequestBody City city) {
		try {
			log.debug("city = {}", city.toString());
			Integer updatedCnt = infoService.updateById(city);
			return new ResponseEntity<>(String.format("%d updated", updatedCnt), HttpStatus.OK);
		}catch (Exception e) {
			log.error(e.toString());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ResponseBody
	@PostMapping(value="cityDelete")
	public ResponseEntity<String> cityDelete(@RequestParam(value="id") Integer id) {
		try {
			log.debug("city id = {}", id);
			Integer deletedCnt = infoService.deleteById(id);
			return new ResponseEntity<>(String.format("%d deleted", deletedCnt), HttpStatus.OK);
		}catch (Exception e) {
			log.error(e.toString());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
//	@PostMapping(value="cityAdd")
//	public ResponseEntity<String> cityAdd(String name, String countryCode, String district, Integer population) {
//		try {
//			log.debug("name = {}, ctCode = {}, district = {}, population ={}", name, countryCode, district, population);
//			
//			log.debug(name.toString());	// null 이면 오류발생
//		}catch (Exception e) {
//			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//		
//		return new ResponseEntity<>("", HttpStatus.OK);
//	}
	
	
	// @ResponseBody 생략가능. class 에 @RestController
	@PostMapping(value="uploadFile")
	public ResponseEntity<String> uploadFile(MultipartFile myFile) throws IllegalStateException, IOException{
		
		if( !myFile.isEmpty() ) {
			log.debug("file org name = {}", myFile.getOriginalFilename());
			log.debug("file content type = {}", myFile.getContentType());
			myFile.transferTo(new File(myFile.getOriginalFilename()));
		}
		
		return new ResponseEntity<>("", HttpStatus.OK);
	}
	

	@PostMapping(value="upload")
	public ResponseEntity<String> upload(MultipartFile file) throws IllegalStateException, IOException{
		
		storageService.store(file);
		
		return new ResponseEntity<>("", HttpStatus.OK);
	}
	
	@GetMapping(value="download/{filename:.+}")
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}
	
	@PostMapping(value="deleteAll")
	public ResponseEntity<String> deleteAll(){
		storageService.deleteAll();;
		return new ResponseEntity<>("", HttpStatus.OK);
	}
	
    @GetMapping("fileList")
    public ResponseEntity<List<FileData>> getListFiles() {
        List<FileData> fileInfos = storageService.loadAll()
          .map(path ->{
        	  FileData data = new FileData();
        	  String filename = path.getFileName().toString();
        	  data.setFilename(filename);
        	  data.setUrl(MvcUriComponentsBuilder.fromMethodName(InfoController.class,
						"serveFile", filename).build().toString());
        	  try {
				data.setSize(Files.size(path));
			} catch (IOException e) {
				log.error(e.getMessage());
			}
        	  return data;
          })
          .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
    }

}
