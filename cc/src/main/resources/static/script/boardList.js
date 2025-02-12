fetch("http://localhost:80/board/list")
      .then(response => response.json())
      .then(data => {
          const boardTableBody = document.getElementById("boardTableBody");

          data.forEach(board => {
              const row = document.createElement("tr");

              // ID
              const idCell = document.createElement("td");
              idCell.textContent = board.id;
              row.appendChild(idCell);

              // Title
              const titleCell = document.createElement("td");
              titleCell.textContent = board.title;
              row.appendChild(titleCell);

              // Start Time
              const startTimeCell = document.createElement("td");
              startTimeCell.textContent = board.startTime; // start_time → startTime 수정
              row.appendChild(startTimeCell);

              const tagNameCell = document.createElement("td");
              tagNameCell.textContent = board.tagName;
              row.appendChild(tagNameCell);


              // Image
              const imgCell = document.createElement("td");
              if (board.base64ImgFile) {
                  const img = document.createElement("img");
                  img.src = board.base64ImgFile;  // Data URI 직접 사용
                  img.style.maxWidth = "100px";
                  imgCell.appendChild(img);
              } else {
                  imgCell.textContent = "No Image";
              }
              row.appendChild(imgCell);

              boardTableBody.appendChild(row);
          });
      })
      .catch(error => console.error("Error fetching board list:", error));