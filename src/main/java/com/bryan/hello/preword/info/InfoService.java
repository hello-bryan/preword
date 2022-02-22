package com.bryan.hello.preword.info;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import com.bryan.hello.preword.info.model.Project;
import com.bryan.hello.preword.info.model.City;
import com.bryan.hello.preword.info.repository.CityRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InfoService {
	
	private final CityRepository cityRepository;
	
	// spring 4.2ddddd
	public InfoService(CityRepository cityRepository) {
		this.cityRepository = cityRepository;
	}
	
	public Project getProjectInfo() {
		
		Project project = new Project();
		project.projectName = "preword";
		project.author = "hello-bryan";
		project.createdDate = new Date();
		
		return project;
	}
	
	public List<City> getCityList() {
		return this.cityRepository.findList();
	}

	public List<City> findCityByCodeAndPopulation(String countryCode, int population) {
		log.debug("countryCode = {}, population = {}", countryCode, population);
		return this.cityRepository.findByCountryCodeAndPopulation(countryCode, population);
	}

	public City insert(City city) {
		return this.cityRepository.insert(city);
	}
	
	public Integer updateById(City city) {
		log.debug("city id = {}", city.getId());
		return cityRepository.updateById(city);
	}
	
	public Integer deleteById(Integer id) {
		log.debug("city id = {}", id);
		return cityRepository.deleteById(id);
	}
}
