package kml;
public class ParsingStructure {
          private String name;
          private String description;
          private String coordinates;
          private boolean isStation;
          
          public boolean isStation() {
			return isStation;
		}

		public void setStation(boolean isStation) {
			this.isStation = isStation;
		}

		public String getName() {
              return name;
          }
       
          public void setName(String name) {
              this.name = name;
          }
       
          public String getDescription() {
              return description;
          }
       
          public void setDescription(String description) {
              this.description = description;
          }
       
          public String getCoordinates() {
              return coordinates;
          }
       
          public void setCoordinates(String coordinates) {
              this.coordinates = coordinates;
              if(this.name.trim().equalsIgnoreCase("Elimbah Creek (east)"))
                  System.out.println(coordinates);
          }
}