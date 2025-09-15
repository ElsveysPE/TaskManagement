import React, { useState, useEffect } from 'react';
import './App.css';

const API_BASE = 'http://localhost:8081/TaskManagement';

// API utility functions
const api = {
  request: async (url, options = {}) => {
    const token = localStorage.getItem('token');
    const config = {
      headers: {
        'Content-Type': 'application/json',
        ...(token && { 'Authorization': `Bearer ${token}` }),
        ...options.headers
      },
      ...options
    };

    try {
      const response = await fetch(`${API_BASE}${url}`, config);

      if (response.status === 401) {
        localStorage.removeItem('token');
        window.location.reload();
        return;
      }

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }

      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        return await response.json();
      }
      return response;
    } catch (error) {
      console.error('API request failed:', error);
      throw error;
    }
  },

  get: (url) => api.request(url),
  post: (url, data) => api.request(url, { method: 'POST', body: JSON.stringify(data) }),
  put: (url, data) => api.request(url, { method: 'PUT', body: JSON.stringify(data) }),
  delete: (url) => api.request(url, { method: 'DELETE' })
};

// Auth Components
const Login = ({ onSuccess, onRegister, error }) => {
  const [form, setForm] = useState({ alias: '', password: '' });
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const response = await api.post('/auth/login', form);
      localStorage.setItem('token', response.token);
      onSuccess(response.token);
    } catch (err) {
      onSuccess(null, 'Invalid credentials');
    }
    setLoading(false);
  };

  return (
    <div className="auth-container">
      <form className="auth-form" onSubmit={handleSubmit}>
        <h2>Login</h2>
        {error && <div className="error">{error}</div>}
        <input
          type="text"
          placeholder="Alias"
          value={form.alias}
          onChange={(e) => setForm({...form, alias: e.target.value})}
          required
          disabled={loading}
        />
        <input
          type="password"
          placeholder="Password"
          value={form.password}
          onChange={(e) => setForm({...form, password: e.target.value})}
          required
          disabled={loading}
        />
        <button type="submit" disabled={loading}>
          {loading ? 'Logging in...' : 'Login'}
        </button>
        <p>Don't have an account? <span onClick={onRegister}>Register</span></p>
      </form>
    </div>
  );
};

const Register = ({ onLogin, error }) => {
  const [form, setForm] = useState({
    username: '', email: '', alias: '', password: '', specialization: ''
  });
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await api.post('/auth/register', form);
      onLogin('Registration successful! Please login.');
    } catch (err) {
      onLogin('Registration failed');
    }
    setLoading(false);
  };

  return (
    <div className="auth-container">
      <form className="auth-form" onSubmit={handleSubmit}>
        <h2>Register</h2>
        {error && <div className="error">{error}</div>}
        <input
          type="text"
          placeholder="Username"
          value={form.username}
          onChange={(e) => setForm({...form, username: e.target.value})}
          required
          disabled={loading}
        />
        <input
          type="email"
          placeholder="Email"
          value={form.email}
          onChange={(e) => setForm({...form, email: e.target.value})}
          required
          disabled={loading}
        />
        <input
          type="text"
          placeholder="Alias"
          value={form.alias}
          onChange={(e) => setForm({...form, alias: e.target.value})}
          required
          disabled={loading}
        />
        <input
          type="password"
          placeholder="Password"
          value={form.password}
          onChange={(e) => setForm({...form, password: e.target.value})}
          required
          disabled={loading}
        />
        <input
          type="text"
          placeholder="Specialization"
          value={form.specialization}
          onChange={(e) => setForm({...form, specialization: e.target.value})}
          required
          disabled={loading}
        />
        <button type="submit" disabled={loading}>
          {loading ? 'Registering...' : 'Register'}
        </button>
        <p>Already have an account? <span onClick={onLogin}>Login</span></p>
      </form>
    </div>
  );
};

// Dashboard Component
const Dashboard = ({ userRole, userProfile }) => {
  const [stats, setStats] = useState({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  }, [userRole]);

  const loadDashboardData = async () => {
    setLoading(true);
    try {
      const data = {};

      if (userRole === 'ADMIN') {
        const [users, projects, tasks] = await Promise.all([
          api.get('/users'),
          api.get('/projects'),
          api.get('/tasks')
        ]);
        data.totalUsers = users.filter(u => !u.deleted).length;
        data.totalProjects = projects.filter(p => !p.deleted).length;
        data.totalTasks = tasks.filter(t => !t.deleted).length;
        data.activeProjects = projects.filter(p => p.statusProject === 'ACTIVE' && !p.deleted).length;
      } else if (userRole === 'TEAMLEAD') {
        const projects = await api.get('/projects/mine');
        data.myProjects = projects.length;
        data.activeProjects = projects.filter(p => p.statusProject === 'ACTIVE').length;

        if (projects.length > 0) {
          const allTasks = await Promise.all(
            projects.map(p => api.get(`/tasks/project/${p.id}`))
          );
          const tasks = allTasks.flat();
          data.totalTasks = tasks.length;
          data.doneTasks = tasks.filter(t => t.status === 'DONE').length;
        } else {
          data.totalTasks = 0;
          data.doneTasks = 0;
        }
      } else {
        // USER - get their task count and profile data
        try {
          const [profileData, userTasks] = await Promise.all([
            api.get('/profile/me'),
            api.get('/profile/me/tasks')
          ]);
          data.myTasks = userTasks.length || 0;
          data.specialization = profileData.specialization || 'N/A';
        } catch (error) {
          console.error('Failed to load user data:', error);
          data.myTasks = 0;
          data.specialization = userProfile?.specialization || 'N/A';
        }
      }

      setStats(data);
    } catch (error) {
      console.error('Failed to load dashboard data:', error);
    }
    setLoading(false);
  };

  if (loading) return <div className="loading">Loading dashboard...</div>;

  return (
    <div>
      <h1>Dashboard</h1>
      <div className="stats">
        {userRole === 'ADMIN' && (
          <>
            <div className="stat-card">
              <h3>Total Users</h3>
              <div className="stat-number">{stats.totalUsers || 0}</div>
            </div>
            <div className="stat-card">
              <h3>Total Projects</h3>
              <div className="stat-number">{stats.totalProjects || 0}</div>
            </div>
            <div className="stat-card">
              <h3>Active Projects</h3>
              <div className="stat-number">{stats.activeProjects || 0}</div>
            </div>
            <div className="stat-card">
              <h3>Total Tasks</h3>
              <div className="stat-number">{stats.totalTasks || 0}</div>
            </div>
          </>
        )}

        {userRole === 'TEAMLEAD' && (
          <>
            <div className="stat-card">
              <h3>My Projects</h3>
              <div className="stat-number">{stats.myProjects || 0}</div>
            </div>
            <div className="stat-card">
              <h3>Active Projects</h3>
              <div className="stat-number">{stats.activeProjects || 0}</div>
            </div>
            <div className="stat-card">
              <h3>Total Tasks</h3>
              <div className="stat-number">{stats.totalTasks || 0}</div>
            </div>
            <div className="stat-card">
              <h3>Completed Tasks</h3>
              <div className="stat-number">{stats.doneTasks || 0}</div>
            </div>
          </>
        )}

        {userRole === 'USER' && (
          <>
            <div className="stat-card">
              <h3>My Tasks</h3>
              <div className="stat-number">{stats.myTasks || 0}</div>
            </div>
            <div className="stat-card">
              <h3>Specialization</h3>
              <div className="stat-text">{stats.specialization}</div>
            </div>
          </>
        )}
      </div>
    </div>
  );
};

// Profile Component
const Profile = () => {
  const [profile, setProfile] = useState(null);
  const [editing, setEditing] = useState(false);
  const [changingPassword, setChangingPassword] = useState(false);
  const [form, setForm] = useState({});
  const [passwordForm, setPasswordForm] = useState({
    currentPassword: '', newPassword: ''
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    loadProfile();
  }, []);

  const loadProfile = async () => {
    try {
      const data = await api.get('/profile/me');
      setProfile(data);
      setForm({...data, currentPassword: ''});
    } catch (error) {
      setError('Failed to load profile');
    }
  };

  const updateProfile = async (e) => {
    e.preventDefault();
    try {
      const url = `/profile/me?currentPassword=${encodeURIComponent(form.currentPassword)}`;
      await api.put(url, form);
      setProfile(form);
      setEditing(false);
      setSuccess('Profile updated successfully');
      setError('');
    } catch (error) {
      setError('Failed to update profile');
    }
  };

  const updatePassword = async (e) => {
    e.preventDefault();
    try {
      const url = `/profile/me/password?currentPassword=${encodeURIComponent(passwordForm.currentPassword)}&newPassword=${encodeURIComponent(passwordForm.newPassword)}`;
      await api.put(url);
      setChangingPassword(false);
      setPasswordForm({ currentPassword: '', newPassword: '' });
      setSuccess('Password updated successfully');
      setError('');
    } catch (error) {
      setError('Failed to update password');
    }
  };

  if (!profile) return <div className="loading">Loading profile...</div>;

  return (
    <div>
      <h1>Profile</h1>

      {error && <div className="error">{error}</div>}
      {success && <div className="success">{success}</div>}

      {!editing && !changingPassword && (
        <div className="profile-view">
          <p><strong>Alias:</strong> {profile.alias}</p>
          <p><strong>Username:</strong> {profile.username}</p>
          <p><strong>Email:</strong> {profile.email}</p>
          <p><strong>Specialization:</strong> {profile.specialization}</p>
          <p><strong>Role:</strong> {profile.role}</p>

          <div style={{marginTop: '20px'}}>
            <button onClick={() => setEditing(true)} className="btn">Edit Profile</button>
            <button onClick={() => setChangingPassword(true)} className="btn">Change Password</button>
          </div>
        </div>
      )}

      {editing && (
        <form onSubmit={updateProfile} className="form">
          <h2>Edit Profile</h2>
          <input
            type="text"
            placeholder="Alias"
            value={form.alias}
            onChange={(e) => setForm({...form, alias: e.target.value})}
            required
          />
          <input
            type="text"
            placeholder="Username"
            value={form.username}
            onChange={(e) => setForm({...form, username: e.target.value})}
            required
          />
          <input
            type="email"
            placeholder="Email"
            value={form.email}
            onChange={(e) => setForm({...form, email: e.target.value})}
            required
          />
          <input
            type="text"
            placeholder="Specialization"
            value={form.specialization}
            onChange={(e) => setForm({...form, specialization: e.target.value})}
            required
          />
          <input
            type="password"
            placeholder="Current Password (required)"
            value={form.currentPassword}
            onChange={(e) => setForm({...form, currentPassword: e.target.value})}
            required
          />
          <div>
            <button type="submit" className="btn">Save</button>
            <button type="button" onClick={() => setEditing(false)} className="btn-cancel">Cancel</button>
          </div>
        </form>
      )}

      {changingPassword && (
        <form onSubmit={updatePassword} className="form">
          <h2>Change Password</h2>
          <input
            type="password"
            placeholder="Current Password"
            value={passwordForm.currentPassword}
            onChange={(e) => setPasswordForm({...passwordForm, currentPassword: e.target.value})}
            required
          />
          <input
            type="password"
            placeholder="New Password"
            value={passwordForm.newPassword}
            onChange={(e) => setPasswordForm({...passwordForm, newPassword: e.target.value})}
            required
          />
          <div>
            <button type="submit" className="btn">Update Password</button>
            <button type="button" onClick={() => setChangingPassword(false)} className="btn-cancel">Cancel</button>
          </div>
        </form>
      )}
    </div>
  );
};

// Projects Component
const Projects = ({ userRole }) => {
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingProject, setEditingProject] = useState(null);
  const [showDeleted, setShowDeleted] = useState(false);

  useEffect(() => {
    loadProjects();
  }, [userRole]);

  const loadProjects = async () => {
    try {
      const endpoint = userRole === 'ADMIN' ? '/projects' : '/projects/mine';
      const data = await api.get(endpoint);
      setProjects(data);
    } catch (error) {
      console.error('Failed to load projects:', error);
    }
    setLoading(false);
  };

  const deleteProject = async (id) => {
    if (!confirm('Delete this project?')) return;
    try {
      await api.delete(`/projects/${id}`);
      loadProjects();
    } catch (error) {
      console.error('Failed to delete project:', error);
    }
  };

  const restoreProject = async (id) => {
    try {
      await api.put(`/projects/${id}/restore`);
      loadProjects();
    } catch (error) {
      console.error('Failed to restore project:', error);
    }
  };

  if (loading) return <div className="loading">Loading projects...</div>;

  const filteredProjects = showDeleted ? projects : projects.filter(p => !p.deleted);

  return (
    <div>
      <div className="page-header">
        <h1>Projects</h1>
        <div>
          {userRole === 'ADMIN' && (
            <label className="checkbox-label">
              <input
                type="checkbox"
                checked={showDeleted}
                onChange={(e) => setShowDeleted(e.target.checked)}
              />
              Show deleted
            </label>
          )}
          {(userRole === 'ADMIN' || userRole === 'TEAMLEAD') && (
            <button onClick={() => setShowForm(true)} className="btn">New Project</button>
          )}
        </div>
      </div>

      {showForm && (
        <ProjectForm
          project={editingProject}
          onSave={() => {
            setShowForm(false);
            setEditingProject(null);
            loadProjects();
          }}
          onCancel={() => {
            setShowForm(false);
            setEditingProject(null);
          }}
        />
      )}

      <table className="data-table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Description</th>
            <th>Status</th>
            <th>Deadline</th>
            <th>Team Lead</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {filteredProjects.map(project => (
            <tr key={project.id}>
              <td>{project.name}</td>
              <td>{project.description}</td>
              <td>
                <span className={`status ${project.statusProject?.toLowerCase()}`}>
                  {project.statusProject}
                </span>
              </td>
              <td>{project.deadline}</td>
              <td>{project.teamlead?.username}</td>
              <td>
                {!project.deleted && (
                  <>
                    <button
                      onClick={() => {
                        setEditingProject(project);
                        setShowForm(true);
                      }}
                      className="btn-edit"
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => deleteProject(project.id)}
                      className="btn-delete"
                    >
                      Delete
                    </button>
                  </>
                )}
                {project.deleted && userRole === 'ADMIN' && (
                  <button
                    onClick={() => restoreProject(project.id)}
                    className="btn-restore"
                  >
                    Restore
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

// Project Form Component
const ProjectForm = ({ project, onSave, onCancel }) => {
  const [form, setForm] = useState({
    name: '',
    description: '',
    statusProject: 'ACTIVE',
    deadline: '',
    teamlead: null
  });
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (project) {
      setForm({
        ...project,
        deadline: project.deadline || '',
        teamlead: project.teamlead || null
      });
    }
    loadUsers();
  }, [project]);

  const loadUsers = async () => {
    try {
      const data = await api.get('/users');
      setUsers(data.filter(u => u.role === 'TEAMLEAD'));
    } catch (error) {
      console.error('Failed to load users:', error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      if (project) {
        await api.put(`/projects/${project.id}`, form);
      } else {
        await api.post('/projects', form);
      }
      onSave();
    } catch (error) {
      console.error('Failed to save project:', error);
    }
    setLoading(false);
  };

  return (
    <form onSubmit={handleSubmit} className="form">
      <h2>{project ? 'Edit Project' : 'New Project'}</h2>

      <input
        type="text"
        placeholder="Project Name"
        value={form.name}
        onChange={(e) => setForm({...form, name: e.target.value})}
        required
        disabled={loading}
      />

      <input
        type="text"
        placeholder="Description"
        value={form.description}
        onChange={(e) => setForm({...form, description: e.target.value})}
        required
        disabled={loading}
      />

      <select
        value={form.statusProject}
        onChange={(e) => setForm({...form, statusProject: e.target.value})}
        required
        disabled={loading}
      >
        <option value="ACTIVE">Active</option>
        <option value="COMPLETED">Completed</option>
      </select>

      <input
        type="date"
        value={form.deadline}
        onChange={(e) => setForm({...form, deadline: e.target.value})}
        required
        disabled={loading}
      />

      <select
        value={form.teamlead?.id || ''}
        onChange={(e) => {
          const user = users.find(u => u.id === parseInt(e.target.value));
          setForm({...form, teamlead: user});
        }}
        required
        disabled={loading}
      >
        <option value="">Select Team Lead</option>
        {users.map(user => (
          <option key={user.id} value={user.id}>{user.name}</option>
        ))}
      </select>

      <div>
        <button type="submit" className="btn" disabled={loading}>
          {loading ? 'Saving...' : 'Save'}
        </button>
        <button type="button" onClick={onCancel} className="btn-cancel" disabled={loading}>
          Cancel
        </button>
      </div>
    </form>
  );
};

// Tasks Component
const Tasks = ({ userRole, userProfile }) => {
  const [tasks, setTasks] = useState([]);
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingTask, setEditingTask] = useState(null);
  const [showDeleted, setShowDeleted] = useState(false);
  const [selectedProject, setSelectedProject] = useState('');

  useEffect(() => {
    loadData();
  }, [userRole]);

  const loadData = async () => {
    try {
      if (userRole === 'ADMIN') {
        const [tasksData, projectsData] = await Promise.all([
          api.get('/tasks'),
          api.get('/projects')
        ]);
        setTasks(tasksData);
        setProjects(projectsData);
      } else if (userRole === 'TEAMLEAD') {
        const projectsData = await api.get('/projects/mine');
        setProjects(projectsData);

        if (projectsData.length > 0) {
          const allTasks = await Promise.all(
            projectsData.map(p => api.get(`/tasks/project/${p.id}`))
          );
          setTasks(allTasks.flat());
        } else {
          setTasks([]);
        }
      } else {
        // USER - get their assigned tasks
        const userTasks = await api.get('/profile/me/tasks');
        setTasks(userTasks);
        setProjects([]);
      }
    } catch (error) {
      console.error('Failed to load data:', error);
    }
    setLoading(false);
  };

  const deleteTask = async (id) => {
    if (!confirm('Delete this task?')) return;
    try {
      await api.delete(`/tasks/${id}`);
      loadData();
    } catch (error) {
      console.error('Failed to delete task:', error);
    }
  };

  const restoreTask = async (id) => {
    try {
      await api.put(`/tasks/${id}/restore`);
      loadData();
    } catch (error) {
      console.error('Failed to restore task:', error);
    }
  };

  if (loading) return <div className="loading">Loading tasks...</div>;

  let filteredTasks = showDeleted ? tasks : tasks.filter(t => !t.deleted);

  if (selectedProject) {
    filteredTasks = filteredTasks.filter(t => t.projectId === parseInt(selectedProject));
  }

  return (
    <div>
      <div className="page-header">
        <h1>Tasks</h1>
        <div>
          {userRole === 'ADMIN' && (
            <label className="checkbox-label">
              <input
                type="checkbox"
                checked={showDeleted}
                onChange={(e) => setShowDeleted(e.target.checked)}
              />
              Show deleted
            </label>
          )}
          {(userRole === 'ADMIN' || userRole === 'TEAMLEAD') && (
            <>
              <select
                value={selectedProject}
                onChange={(e) => setSelectedProject(e.target.value)}
                className="filter-select"
              >
                <option value="">All Projects</option>
                {projects.map(p => (
                  <option key={p.id} value={p.id}>{p.name}</option>
                ))}
              </select>
              <button onClick={() => setShowForm(true)} className="btn">New Task</button>
            </>
          )}
        </div>
      </div>

      {showForm && (
        <TaskForm
          task={editingTask}
          projects={projects}
          onSave={() => {
            setShowForm(false);
            setEditingTask(null);
            loadData();
          }}
          onCancel={() => {
            setShowForm(false);
            setEditingTask(null);
          }}
        />
      )}

      <table className="data-table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Description</th>
            <th>Status</th>
            <th>Project</th>
            <th>Deadline</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {filteredTasks.map(task => (
            <tr key={task.id}>
              <td>{task.name}</td>
              <td>{task.description}</td>
              <td>
                <span className={`status ${task.status?.toLowerCase()}`}>
                  {task.status?.replace('_', ' ')}
                </span>
              </td>
              <td>{projects.find(p => p.id === task.projectId)?.name || 'N/A'}</td>
              <td>
                {task.assignments && task.assignments.length > 0
                  ? task.assignments
                      .filter(a => !a.deleted && a.deadline) // Filter out deleted assignments
                      .map(a => a.deadline)
                      .join(', ') || 'No active assignments'
                  : 'No assignments'}
              </td>
              <td>
                {userRole !== 'USER' && !task.deleted && (userRole === 'ADMIN' || userRole === 'TEAMLEAD') && (
                  <>
                    <button
                      onClick={() => {
                        setEditingTask(task);
                        setShowForm(true);
                      }}
                      className="btn-edit"
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => deleteTask(task.id)}
                      className="btn-delete"
                    >
                      Delete
                    </button>
                  </>
                )}
                {task.deleted && userRole === 'ADMIN' && (
                  <button
                    onClick={() => restoreTask(task.id)}
                    className="btn-restore"
                  >
                    Restore
                  </button>
                )}
                {userRole === 'USER' && <span>View only</span>}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

// Task Form Component
const TaskForm = ({ task, projects, onSave, onCancel }) => {
  const [form, setForm] = useState({
    name: '',
    description: '',
    status: 'TO_DO',
    projectId: '',
    assignments: []
  });
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (task) {
      setForm({
        ...task,
        projectId: task.projectId || '',
        assignments: task.assignments || []
      });
    }
    loadUsers();
  }, [task]);

  const loadUsers = async () => {
    try {
      const data = await api.get('/users');
      setUsers(data);
    } catch (error) {
      console.error('Failed to load users:', error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      if (task) {
        await api.put(`/tasks/${task.id}`, form);
      } else {
        await api.post('/tasks', form);
      }
      onSave();
    } catch (error) {
      console.error('Failed to save task:', error);
    }
    setLoading(false);
  };

  const addAssignment = () => {
    setForm({
      ...form,
      assignments: [...form.assignments, { user: null, deadline: '' }]
    });
  };

  const updateAssignment = (index, field, value) => {
    const newAssignments = [...form.assignments];
    if (field === 'user') {
      newAssignments[index] = { ...newAssignments[index], user: users.find(u => u.id === parseInt(value)) };
    } else {
      newAssignments[index] = { ...newAssignments[index], [field]: value };
    }
    setForm({ ...form, assignments: newAssignments });
  };

  const removeAssignment = (index) => {
    setForm({
      ...form,
      assignments: form.assignments.filter((_, i) => i !== index)
    });
  };

  return (
    <form onSubmit={handleSubmit} className="form">
      <h2>{task ? 'Edit Task' : 'New Task'}</h2>

      <input
        type="text"
        placeholder="Task Name"
        value={form.name}
        onChange={(e) => setForm({...form, name: e.target.value})}
        required
        disabled={loading}
      />

      <textarea
        placeholder="Description"
        value={form.description}
        onChange={(e) => setForm({...form, description: e.target.value})}
        required
        disabled={loading}
        rows="3"
      />

      <select
        value={form.status}
        onChange={(e) => setForm({...form, status: e.target.value})}
        required
        disabled={loading}
      >
        <option value="TO_DO">To Do</option>
        <option value="IN_PROGRESS">In Progress</option>
        <option value="DONE">Done</option>
      </select>

      <select
        value={form.projectId}
        onChange={(e) => setForm({...form, projectId: parseInt(e.target.value)})}
        required
        disabled={loading}
      >
        <option value="">Select Project</option>
        {projects.map(project => (
          <option key={project.id} value={project.id}>{project.name}</option>
        ))}
      </select>

      <div>
        <h3>Assignments</h3>
        {form.assignments.map((assignment, index) => (
          <div key={index} className="assignment-row">
            <select
              value={assignment.user?.id || ''}
              onChange={(e) => updateAssignment(index, 'user', e.target.value)}
              required
              disabled={loading}
            >
              <option value="">Select User</option>
              {users.map(user => (
                <option key={user.id} value={user.id}>{user.name}</option>
              ))}
            </select>
            <input
              type="date"
              value={assignment.deadline || ''}
              onChange={(e) => updateAssignment(index, 'deadline', e.target.value)}
              required
              disabled={loading}
            />
            <button
              type="button"
              onClick={() => removeAssignment(index)}
              className="btn-delete"
              disabled={loading}
            >
              Remove
            </button>
          </div>
        ))}
        <button
          type="button"
          onClick={addAssignment}
          className="btn"
          disabled={loading}
        >
          Add Assignment
        </button>
      </div>

      <div>
        <button type="submit" className="btn" disabled={loading}>
          {loading ? 'Saving...' : 'Save'}
        </button>
        <button type="button" onClick={onCancel} className="btn-cancel" disabled={loading}>
          Cancel
        </button>
      </div>
    </form>
  );
};

// Users Component (Admin only)
const Users = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showDeleted, setShowDeleted] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [editingUser, setEditingUser] = useState(null);

  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    try {
      const data = await api.get('/users');
      setUsers(data);
    } catch (error) {
      console.error('Failed to load users:', error);
    }
    setLoading(false);
  };

  const deleteUser = async (id) => {
    if (!confirm('Delete this user?')) return;
    try {
      await api.delete(`/users/${id}`);
      loadUsers();
    } catch (error) {
      console.error('Failed to delete user:', error);
    }
  };

  const restoreUser = async (id) => {
    try {
      await api.put(`/users/${id}/restore`);
      loadUsers();
    } catch (error) {
      console.error('Failed to restore user:', error);
    }
  };

  if (loading) return <div className="loading">Loading users...</div>;

  const filteredUsers = showDeleted ? users : users.filter(u => !u.deleted);

  return (
    <div>
      <div className="page-header">
        <h1>Users</h1>
        <label className="checkbox-label">
          <input
            type="checkbox"
            checked={showDeleted}
            onChange={(e) => setShowDeleted(e.target.checked)}
          />
          Show deleted
        </label>
      </div>

      {showForm && (
        <UserForm
          user={editingUser}
          onSave={() => {
            setShowForm(false);
            setEditingUser(null);
            loadUsers();
          }}
          onCancel={() => {
            setShowForm(false);
            setEditingUser(null);
          }}
        />
      )}

      <table className="data-table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Role</th>
            <th>Specialization</th>
            <th>Tasks</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {filteredUsers.map(user => (
            <tr key={user.id}>
              <td>{user.name}</td>
              <td>{user.email}</td>
              <td>
                <span className={`status ${user.role?.toLowerCase()}`}>
                  {user.role}
                </span>
              </td>
              <td>{user.specialization}</td>
              <td>{user.tasks?.length || 0}</td>
              <td>
                {!user.deleted && (
                  <>
                    <button
                      onClick={() => {
                        setEditingUser(user);
                        setShowForm(true);
                      }}
                      className="btn-edit"
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => deleteUser(user.id)}
                      className="btn-delete"
                    >
                      Delete
                    </button>
                  </>
                )}
                {user.deleted && (
                  <button
                    onClick={() => restoreUser(user.id)}
                    className="btn-restore"
                  >
                    Restore
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

// User Form Component
const UserForm = ({ user, onSave, onCancel }) => {
  const [form, setForm] = useState({
    name: '',
    email: '',
    role: 'USER',
    specialization: ''
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (user) {
      setForm({
        name: user.name || '',
        email: user.email || '',
        role: user.role || 'USER',
        specialization: user.specialization || ''
      });
    }
  }, [user]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await api.put(`/users/${user.id}`, form);
      onSave();
    } catch (error) {
      console.error('Failed to save user:', error);
    }
    setLoading(false);
  };

  return (
    <form onSubmit={handleSubmit} className="form">
      <h2>Edit User</h2>

      <input
        type="text"
        placeholder="Name"
        value={form.name}
        onChange={(e) => setForm({...form, name: e.target.value})}
        required
        disabled={loading}
      />

      <input
        type="email"
        placeholder="Email"
        value={form.email}
        onChange={(e) => setForm({...form, email: e.target.value})}
        required
        disabled={loading}
      />

      <select
        value={form.role}
        onChange={(e) => setForm({...form, role: e.target.value})}
        required
        disabled={loading}
      >
        <option value="USER">User</option>
        <option value="TEAMLEAD">Team Lead</option>
        <option value="ADMIN">Admin</option>
      </select>

      <input
        type="text"
        placeholder="Specialization"
        value={form.specialization}
        onChange={(e) => setForm({...form, specialization: e.target.value})}
        required
        disabled={loading}
      />

      <div>
        <button type="submit" className="btn" disabled={loading}>
          {loading ? 'Saving...' : 'Save'}
        </button>
        <button type="button" onClick={onCancel} className="btn-cancel" disabled={loading}>
          Cancel
        </button>
      </div>
    </form>
  );
};

// Main App Component
const App = () => {
  const [currentPage, setCurrentPage] = useState('login');
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [userRole, setUserRole] = useState('');
  const [userProfile, setUserProfile] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        setUserRole(payload.role);
        setCurrentPage('dashboard');
        loadUserProfile();
      } catch (err) {
        logout();
      }
    }
  }, [token]);

  const loadUserProfile = async () => {
    try {
      const data = await api.get('/profile/me');
      setUserProfile(data);
    } catch (error) {
      console.error('Failed to load profile:', error);
    }
  };

  const handleLogin = (newToken, loginError) => {
    if (newToken) {
      setToken(newToken);
      setError('');
    } else {
      setError(loginError || 'Login failed');
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    setToken(null);
    setCurrentPage('login');
    setUserRole('');
    setUserProfile(null);
  };

  const getMenuItems = () => {
    const items = [
      { key: 'dashboard', label: 'Dashboard', roles: ['ADMIN', 'TEAMLEAD', 'USER'] },
      { key: 'profile', label: 'Profile', roles: ['ADMIN', 'TEAMLEAD', 'USER'] },
      { key: 'projects', label: 'Projects', roles: ['ADMIN', 'TEAMLEAD'] },
      { key: 'tasks', label: 'Tasks', roles: ['ADMIN', 'TEAMLEAD', 'USER'] },
      { key: 'users', label: 'Users', roles: ['ADMIN'] }
    ];

    return items.filter(item => item.roles.includes(userRole));
  };

  if (!token) {
    return currentPage === 'login' ? (
      <Login
        onSuccess={handleLogin}
        onRegister={() => setCurrentPage('register')}
        error={error}
      />
    ) : (
      <Register
        onLogin={(message) => {
          setCurrentPage('login');
          setError(message);
        }}
        error={error}
      />
    );
  }

  return (
    <div className="app">
      <nav className="sidebar">
        <h2>Task Manager</h2>
        <ul>
          {getMenuItems().map(item => (
            <li
              key={item.key}
              className={currentPage === item.key ? 'active' : ''}
              onClick={() => setCurrentPage(item.key)}
            >
              {item.label}
            </li>
          ))}
          <li onClick={logout}>Logout</li>
        </ul>
      </nav>

      <main className="main-content">
        {currentPage === 'dashboard' && <Dashboard userRole={userRole} userProfile={userProfile} />}
        {currentPage === 'profile' && <Profile />}
        {currentPage === 'projects' && <Projects userRole={userRole} />}
        {currentPage === 'tasks' && <Tasks userRole={userRole} userProfile={userProfile} />}
        {currentPage === 'users' && <Users />}
      </main>
    </div>
  );
};

export default App;