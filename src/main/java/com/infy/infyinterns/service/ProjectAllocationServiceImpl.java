package com.infy.infyinterns.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.infy.infyinterns.dto.MentorDTO;
import com.infy.infyinterns.dto.ProjectDTO;
import com.infy.infyinterns.exception.InfyInternException;
import com.infy.infyinterns.repository.MentorRepository;
import com.infy.infyinterns.repository.ProjectRepository;
import com.infy.infyinterns.entity.Mentor;
import com.infy.infyinterns.entity.Project;

@Service(value = "projectService")
@Transactional
public class ProjectAllocationServiceImpl implements ProjectAllocationService {

	@Autowired
	ProjectRepository projectRepository;
	@Autowired
	MentorRepository mentorRepository;
	
	
	
	@Override
	public Integer allocateProject(ProjectDTO project) throws InfyInternException {
		Optional<Mentor> optional = mentorRepository.findById(project.getMentorDTO().getMentorId());
		Mentor mentor = optional.orElseThrow(()-> new InfyInternException("Service.MENTOR_NOT_FOUND"));
		
		if(mentor.getNumberOfProjectsMentored()>=3) {
			throw new InfyInternException("Service.CANNOT_ALLOCATE_PROJECT");
		}
		
			Project projectNew = new Project();
			projectNew.setIdeaOwner(project.getIdeaOwner());
			projectNew.setProjectId(project.getProjectId());
			projectNew.setProjectName(project.getProjectName());
			projectNew.setReleaseDate(project.getReleaseDate());
			projectNew.setMentor(mentor);
			mentor.setNumberOfProjectsMentored(mentor.getNumberOfProjectsMentored()+1);
			Project newProject = projectRepository.save(projectNew);
			return newProject.getProjectId();
			
	}

	
	@Override
	public List<MentorDTO> getMentors(Integer numberOfProjectsMentored) throws InfyInternException {
		List<Mentor> list = mentorRepository.numberOfProjectsMentored(numberOfProjectsMentored);
		if(list.isEmpty()) {
			throw new InfyInternException("Service.MENTOR_NOT_FOUND");
		}
		List<MentorDTO> newList = new ArrayList<>();
		for(Mentor mentor : list) {
			MentorDTO newMentor = new MentorDTO();
			newMentor.setMentorId(mentor.getMentorId());
			newMentor.setMentorName(mentor.getMentorName());
			newMentor.setNumberOfProjectsMentored(mentor.getNumberOfProjectsMentored()+1);
			newList.add(newMentor);
		}
		return newList;
		
	}


	@Override
	public void updateProjectMentor(Integer projectId, Integer mentorId) throws InfyInternException {
		Optional<Mentor> optional = mentorRepository.findById(mentorId);
		Mentor mentor = optional.orElseThrow(()-> new InfyInternException("Service.MENTOR_NOT_FOUND"));
		if(mentor.getNumberOfProjectsMentored()>=3) {
			throw new InfyInternException("Service.CANNOT_ALLOCATE_PROJECT");
		}
		Optional<Project> opt = projectRepository.findById(projectId);
		Project project = opt.orElseThrow(()-> new InfyInternException("Service.PROJECT_NOT_FOUND"));
		project.setMentor(mentor);
		mentor.setNumberOfProjectsMentored(mentor.getNumberOfProjectsMentored()+1);
	}

	@Override
	public void deleteProject(Integer projectId) throws InfyInternException {
		Optional<Project> opt = projectRepository.findById(projectId);
		Project project = opt.orElseThrow(()-> new InfyInternException("Service.PROJECT_NOT_FOUND"));
	
		Mentor mentor = project.getMentor();
		mentor.setNumberOfProjectsMentored(mentor.getNumberOfProjectsMentored()-1);
			project.setMentor(null);
			projectRepository.delete(project);
	
	}
}