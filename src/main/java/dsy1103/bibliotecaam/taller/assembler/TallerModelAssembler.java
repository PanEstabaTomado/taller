package dsy1103.bibliotecaam.taller.assembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import dsy1103.bibliotecaam.taller.controller.TallerController;
import dsy1103.bibliotecaam.taller.dto.TallerResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class TallerModelAssembler implements RepresentationModelAssembler<TallerResponseDTO, EntityModel<TallerResponseDTO>> {
    @Override
    public EntityModel<TallerResponseDTO> toModel(TallerResponseDTO tallerDto){
        return EntityModel.of(tallerDto,
                linkTo(methodOn(TallerController.class).obtenerPorId(tallerDto.getIdTaller())).withSelfRel(),
                linkTo(methodOn(TallerController.class).obtenerTodos()).withRel("talleres"));
    }
}
