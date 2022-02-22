package com.bryan.hello.preword.info.repository;

import java.util.List;

import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.bryan.hello.preword.info.model.City;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class CityRepository {
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private final CityRowMapper cityRowMapper;
	
	public CityRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
		this.cityRowMapper = new CityRowMapper();
	}

	public List<City> findList(){
		
		log.debug("findList query = {}", CitySql.SELECT);

		return namedParameterJdbcTemplate.query(CitySql.SELECT
				, EmptySqlParameterSource.INSTANCE
				, this.cityRowMapper);
	}
	
	public List<City> findByCountryCodeAndPopulation(String countryCode, int population){
		
		String qry = CitySql.SELECT
				+ CitySql.COUNTRY_CODE_CONDITION
				+ CitySql.POPULATION_CONDITION;
 
		SqlParameterSource param = new MapSqlParameterSource("countryCode", countryCode)
				.addValue("population", population);
		
		return namedParameterJdbcTemplate.query(qry, param, this.cityRowMapper);
	}

	public City insert(City city) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		SqlParameterSource parameterSource = new MapSqlParameterSource("name", city.getName())
				.addValue("countryCode", city.getCountryCode())
				.addValue("district", city.getDistrict())
				.addValue("population", city.getPopulation()); 
		int affectedRows = namedParameterJdbcTemplate.update(CitySql.INSERT, parameterSource, keyHolder);
		log.debug("{} inserted, new id = {}", affectedRows, keyHolder.getKey());
		city.setId(keyHolder.getKey().intValue());
		return city;
	}
	
	public Integer updateById(City city) {
		String qry = CitySql.UPDATE + CitySql.ID_CONDITION;
		
		SqlParameterSource parameterSource = new MapSqlParameterSource("id", city.getId())
				.addValue("name", city.getName())
				.addValue("countryCode", city.getCountryCode())
				.addValue("district", city.getDistrict())
				.addValue("population", city.getPopulation()); 
		return namedParameterJdbcTemplate.update(qry, parameterSource);
	}
	
	public Integer deleteById(Integer id) {
		SqlParameterSource parameterSource = new MapSqlParameterSource("id", id); 
		return namedParameterJdbcTemplate.update(CitySql.DELETE + CitySql.ID_CONDITION, parameterSource);
	}
}
