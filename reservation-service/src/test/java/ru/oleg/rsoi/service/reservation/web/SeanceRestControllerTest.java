package ru.oleg.rsoi.service.reservation.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.oleg.rsoi.dto.reservation.SeanceRequest;
import ru.oleg.rsoi.dto.reservation.SeanceResponse;
import ru.oleg.rsoi.service.reservation.domain.Seance;
import ru.oleg.rsoi.service.reservation.service.SeanceService;


import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebMvcTest(SeanceRestController.class)
public class SeanceRestControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    SeanceService service;

    @Test
    public void getSeance() throws Exception {
        // Arrange
        Seance seance = new Seance()
                .setId(10)
                .setMovieId(200);

        given(service.getById(10)).willReturn(seance);

        // Act
        MvcResult result = mvc.perform(get("/seance/10").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        SeanceResponse response = mapper.readValue(result.getResponse().getContentAsString(),
                SeanceResponse.class);

        // Assert
        assertThat(seance.toResponse()).isEqualTo(response);
    }

    @Test
    public void getSeancesByMovie() throws Exception {
        // Arrange
        Seance seance = new Seance()
                .setId(10)
                .setMovieId(200);
        Seance seance2 = new Seance()
                .setId(11)
                .setMovieId(200);

        List<Seance> seances = new ArrayList<>();
        seances.add(seance); seances.add(seance2);

        given(service.getByMovie(200)).willReturn(seances);

        // Act
        MvcResult result = mvc.perform(get("/seance?movie=200").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        SeanceResponse[] response = mapper.readValue(result.getResponse().getContentAsString(),
                SeanceResponse[].class);

        // Assert
        assertThat(response).containsExactlyElementsOf(
                seances.stream().map(Seance::toResponse).collect(Collectors.toList()));
    }

    @Test
    public void createSeance() throws Exception {
        // Arrange
        Seance seance = new Seance()
                .setId(100)
                .setMovieId(101)
                .setScreenDate(new Date());
        SeanceRequest request = new SeanceRequest(101, seance.getScreenDate());

        given(service.createSeance(request)).willReturn(seance);

        // Act
        MvcResult result = mvc.perform(post("/seance").contentType(MediaType.APPLICATION_JSON).
                content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/seance/100"))
                .andReturn();

        SeanceResponse response = mapper.readValue(result.getResponse().getContentAsString(), SeanceResponse.class);

        // Assert
        assertThat(response).isEqualTo(seance.toResponse());
    }

    @Test
    public void deleteSeance() throws Exception {
        mvc.perform(delete("/seance/100"))
                .andExpect(status().isNoContent());
        verify(service, times(1)).deleteSeance(100);
    }

}