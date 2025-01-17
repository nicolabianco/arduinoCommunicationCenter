package com.arduino.service.impl;

import com.arduino.dto.ArduinoDTO;
import com.arduino.dto.DataDTO;
import com.arduino.mapper.ArduinoMapper;
import com.arduino.repository.ArduinoRepository;
import com.arduino.service.ArduService;
import com.arduino.service.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


@Service
public class ArduServiceImpl implements ArduService {

    private DataDTO data;

    public ArduServiceImpl() {
        this.data = new DataDTO();
    }

    @Autowired
    private ArduinoRepository arduinoRepository;

    @Autowired
    private TelegramService telegramService;

    public void manageData(ArduinoDTO dto){
        data.setTemperatura(dto.getTemperatura());
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String currentDate = now.format(dateFormatter);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String currentTime = now.format(timeFormatter);
        data.setDataLettura(currentDate);
        data.setOraLettura(currentTime);
        arduinoRepository.save(ArduinoMapper.toEntity(data));
        telegramService.sendMessage(dto.getTemperatura());
    }

    public String showData(Model model){
        String temperaturaDefault = "N/A";
        String dataLetturaDefault = "N/A";
        String oraLetturaDefault = "N/A";

        String temperatura = Objects.requireNonNullElse(data.getTemperatura(), temperaturaDefault);
        String dataLettura = Objects.requireNonNullElse(data.getDataLettura(), dataLetturaDefault);
        String oraLettura = Objects.requireNonNullElse(data.getOraLettura(), oraLetturaDefault);

        model.addAttribute("temperatura", temperatura);
        model.addAttribute("data_lettura", dataLettura);
        model.addAttribute("ora_lettura", oraLettura);
        model.addAttribute("temperatura_minima", arduinoRepository.findTemperaturaMinima());
        model.addAttribute("temperatura_massima", arduinoRepository.findTemperaturaMassima());
        return "pagina";
    }
}
