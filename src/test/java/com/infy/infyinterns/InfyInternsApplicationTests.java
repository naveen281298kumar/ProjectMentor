package com.infy.infyinterns;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import com.infy.infyinterns.dto.MentorDTO;
import com.infy.infyinterns.dto.ProjectDTO;
import com.infy.infyinterns.entity.Mentor;
import com.infy.infyinterns.exception.InfyInternException;
import com.infy.infyinterns.repository.MentorRepository;
import com.infy.infyinterns.service.ProjectAllocationService;
import com.infy.infyinterns.service.ProjectAllocationServiceImpl;

@SpringBootTest
public class InfyInternsApplicationTests {

	@Mock
	private MentorRepository mentorRepository;

	@InjectMocks
	private ProjectAllocationService projectAllocationService = new ProjectAllocationServiceImpl();

	@Test
	public void allocateProjectCannotAllocateTest() throws Exception {
		ProjectDTO p = new ProjectDTO();
		p.setIdeaOwner(10009);
		p.setProjectName("Android Shopping App");
		p.setReleaseDate(LocalDate.of(2019, 9, 25));
		MentorDTO m = new MentorDTO();
		m.setMentorId(1009);
		p.setMentorDTO(m);
		Mentor mentor =new Mentor();
		mentor.setMentorId(1009);
		mentor.setMentorName("Jhon");
		mentor.setNumberOfProjectsMentored(4);
		 
		Mockito.when(mentorRepository.findById(m.getMentorId())).thenReturn(Optional.of(mentor));
		InfyInternException e = Assertions.assertThrows(InfyInternException.class, ()-> projectAllocationService.allocateProject(p));
		Assertions.assertEquals("Service.CANNOT_ALLOCATE_PROJECT", e.getMessage());
	}

	@Test
	public void allocateProjectMentorNotFoundTest() throws Exception {
	
		ProjectDTO p= new ProjectDTO();
		p.setIdeaOwner(1000);
//		p.setProjectId(1007);
		p.setProjectName("Project");
		p.setReleaseDate(LocalDate.of(2019, 9, 25));
		MentorDTO m = new MentorDTO();
		m.setMentorId(1009);
		p.setMentorDTO(m);
		
		Mockito.when(mentorRepository.findById(m.getMentorId())).thenReturn(Optional.empty());
		InfyInternException e = Assertions.assertThrows(InfyInternException.class, ()-> projectAllocationService.allocateProject(p));
		Assertions.assertEquals("Service.MENTOR_NOT_FOUND", e.getMessage());
	}
}