package com.lancenter.lancenterhd.service;

import com.lancenter.lancenterhd.model.Maquina;
import com.lancenter.lancenterhd.repository.MaquinaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaquinaService {

    private final MaquinaRepository maquinaRepository;

    public MaquinaService(MaquinaRepository maquinaRepository) {
        this.maquinaRepository = maquinaRepository;
    }

    public List<Maquina> listarTodas() {
        return maquinaRepository.findAll();
    }
}