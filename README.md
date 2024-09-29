# Video-Queue
Video Processing Task with RabbitMQ, Priority Management, and Locking Mechanism

Objective:
Implement a video upload service that processes videos into two resolutions: low resolution (480p) and high resolution (720p). The system must prioritize 480p processing over 720p, ensuring that 720p processing only occurs when the 480p queue is empty. If a new 480p task arrives, the 720p processing should pause immediately, allowing 480p to take priority.

Requirements:

Video Upload:
The service should accept video uploads through an API and store the original video temporarily.

Task Queue with RabbitMQ:
Use RabbitMQ to manage tasks for processing the video into two versions:

480p (low resolution)
720p (high resolution)
Processing Priority and Locking Mechanism:

480p Priority: The 480p queue should always have higher priority. 720p processing should only begin when the 480p queue is empty.
Pausing 720p: If a new message arrives in the 480p queue while the 720p processing is in progress (in terms of the queue, not FFMPEG), 720p should immediately pause, allowing 480p to process first.
Implement a locking mechanism to handle this queue management and ensure only one queue (480p or 720p) is active at a time.
Video Processing with FFMPEG:
Write FFMPEG commands to process videos into 480p and 720p resolutions. Ensure that these commands are only triggered once the corresponding task is active in the queue.

Completion Status:
After processing each version, update the status in the database (or notify via another mechanism) and store the processed video in a designated location.

Deliverables:

A fully functional service that handles the upload and processing of videos with RabbitMQ task management.
Proper priority handling between 480p and 720p queues, with a locking mechanism to manage the queue execution.
