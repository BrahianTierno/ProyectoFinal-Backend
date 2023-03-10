package com.brahian.proyecto.controller;

import com.brahian.proyecto.dto.TurnoDTO;
import com.brahian.proyecto.exception.BadRequestException;
import com.brahian.proyecto.service.OdontologoService;
import com.brahian.proyecto.service.PacienteService;
import com.brahian.proyecto.service.TurnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/turnos")
public class TurnoController {

    private TurnoService turnoService;
    private PacienteService pacienteService;
    private OdontologoService odontologoService;
    @Autowired
    public TurnoController(TurnoService turnoService, PacienteService pacienteService, OdontologoService odontologoService) {
        this.turnoService = turnoService;
        this.pacienteService = pacienteService;
        this.odontologoService = odontologoService;
    }
    @GetMapping
    public ResponseEntity<List<TurnoDTO>> listarLosTurnos(){
        return ResponseEntity.ok(turnoService.buscarTodosTurno());
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<TurnoDTO> buscarTurno(@PathVariable("id") Long id){
        //tengo dos alternativas.
        Optional<TurnoDTO> turnoBuscado=turnoService.buscarTurno(id);
        if (turnoBuscado.isPresent()){
            return ResponseEntity.ok(turnoBuscado.get());
        }
        else{
            //no existe el turno con el id ingresado
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<TurnoDTO> registarTurno(@RequestBody TurnoDTO turno) throws BadRequestException {
        ResponseEntity<TurnoDTO> respuesta;

        if (pacienteService.buscarPaciente(turno.getPacienteId()).isPresent()&&
                odontologoService.buscarOdontologoXId(turno.getOdontologoId()).isPresent()
        ){
            //ambos existen en la BD
            //podemos registrar el turno sin problemas, indicamos ok (200)
            respuesta=ResponseEntity.ok(turnoService.guardarTurno(turno));
        }
        else{
            //uno o ambos no existen, debemos bloquear la operaci??n
            throw new BadRequestException("No se puede registrar un turno cuando no exista " +
                    "un odontologo y/o un paciente");
            //alternativa para seleccionar cualquier c??digo
            //respuesta=ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return respuesta;

    }
    @PutMapping
    public ResponseEntity<String> actualizarTurno(@RequestBody TurnoDTO turno){
        //verificar que el turno exista
        //control como el post

        ResponseEntity<TurnoDTO> respuesta;

        if(turnoService.buscarTurno(turno.getId()).isPresent()){
            //es un id v??lido
            if (pacienteService.buscarPaciente(turno.getPacienteId()).isPresent()&&
                    odontologoService.buscarOdontologoXId(turno.getOdontologoId()).isPresent()
            ){
                //ambos existen en la BD
                //podemos registrar el turno sin problemas, indicamos ok (200)
                turnoService.actualizarTurno(turno);
                return ResponseEntity.ok("Se actualiz?? el turno con id= "+turno.getId());
            }
            else{
                //uno o ambos no existen, debemos bloquear la operaci??n
                return ResponseEntity.badRequest().body("Error al actualizar, verificar si el" +
                        " odontologo y/o el paciente existen en la base de datos.");
            }
        }
        else{
            //error con el id
            return ResponseEntity.badRequest().body("No se puede actualizar un turno" +
                    " que no exista en la base de datos.");
        }
    }
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarTurno(@PathVariable Long id){
        if (turnoService.buscarTurno(id).isPresent()){
            turnoService.eliminarTurno(id);
            return ResponseEntity.ok().body("Se elimin?? el turno con id= "+id);
        }
        else{
            return ResponseEntity.badRequest().body("No se puede eliminar el turno con id= "+id+
                    " ya que el mismo no existe en la base de datos.");
        }
    }


}

