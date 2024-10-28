package com.hackathon.bankingapp.Utils;

import com.hackathon.bankingapp.DTO.PinCreateDTO;
import com.hackathon.bankingapp.DTO.PinEditDTO;
import com.hackathon.bankingapp.Entities.Pin;

public class PinMapper {

    public PinCreateDTO toCreateDTO(Pin pin) {
        if (pin == null) {
            return null;
        }
        return new PinCreateDTO(
                pin.getPin(),
                pin.getIdentifier()
        );
    }


    public PinEditDTO toEditDTO(Pin pin) {
        if (pin == null) {
            return null;
        }
        return new PinEditDTO(
                pin.getPin(),
                pin.getIdentifier(),
                null
        );
    }


    public Pin toEntity(PinCreateDTO pinCreateDTO) {
        if (pinCreateDTO == null) {
            return null;
        }
        return new Pin(
                null,
                pinCreateDTO.getPin(),
                pinCreateDTO.getPassword()
        );
    }

    public Pin toEntity(PinEditDTO pinEditDTO) {
        if (pinEditDTO == null) {
            return null;
        }
        return new Pin(
                null,
                pinEditDTO.getNewPin(),
                pinEditDTO.getPassword()
        );
    }
}
